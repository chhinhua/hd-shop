package com.duck.service.order.impl;

import com.duck.config.DateTimeConfig;
import com.duck.dto.address.AddressDTO;
import com.duck.dto.ghn.GhnOrder;
import com.duck.dto.order.*;
import com.duck.entity.*;
import com.duck.exception.APIException;
import com.duck.exception.InvalidException;
import com.duck.exception.ResourceNotFoundException;
import com.duck.repository.*;
import com.duck.service.address.AddressService;
import com.duck.service.cart.CartItemService;
import com.duck.service.cart.CartService;
import com.duck.service.ghn.GhnService;
import com.duck.service.order.OrderService;
import com.duck.service.order.OrderTrackingService;
import com.duck.service.product.ProductService;
import com.duck.service.product.ProductSkuService;
import com.duck.service.redis.RedisService;
import com.duck.service.user.UserService;
import com.duck.utils.AppUtils;
import com.duck.utils.enums.EOrderStatus;
import com.duck.utils.enums.EOrderTrackingStatus;
import com.duck.utils.enums.EPaymentType;
import com.duck.validator.OrderValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderServiceImpl implements OrderService {
    private final ProductSkuRepository productSkuRepository;
    AppUtils appUtils;
    ModelMapper modelMapper;
    MessageSource messageSource;
    OrderRepository orderRepository;
    ReviewRepository reviewRepository;
    ProductRepository productRepository;
    CartItemRepository cartItemRepository;
    GhnService ghnService;
    CartService cartService;
    UserService userService;
    ProductSkuService skuService;
    AddressService addressService;
    ProductService productService;
    CartItemService cartItemService;
    OrderTrackingService trackingService;
    OrderValidator validator;
    RedisService<Order> redisService;
    static Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Override
    public void saveChangedSkusQuantity(Order order) {
        order.getOrderItems().forEach(this::updateSkuQuantity);
    }

    void updateSkuQuantity(OrderItem item) {
        ProductSku sku = skuService.findById(item.getSku().getSkuId());
        sku.setQuantityAvailable(sku.getQuantityAvailable() - item.getQuantity());
        skuService.save(sku);
    }

    @Override
    @Transactional
    public void trackingOrder(OrderStatusPayload payload) {
        EOrderTrackingStatus trackingStatus = EOrderTrackingStatus.fromStatus(payload.getStatus());
        Order order = findByOrderCode(payload.getOrderCode());
        // create tracking entity
        OrderTrackingDTO trackingDTO = new OrderTrackingDTO();
        trackingDTO.setTime(payload.getTime());
        trackingDTO.setOrderId(order.getId());
        trackingDTO.setStatus(payload.getStatus());
        trackingDTO.setDescription(trackingStatus.getDescription());
        trackingService.create(trackingDTO);

        // update order status
        EOrderStatus orderStatus = EOrderStatus.fromOrderTrackingStatus(payload.getStatus());
        if (orderStatus != null) {
            order.setStatus(orderStatus);
            orderRepository.save(order);
        }
    }

    @Override
    public Order findByOrderCode(String orderCode) {
        return orderRepository.findByOrderCode(orderCode).orElseThrow(() ->
                new ResourceNotFoundException("%s=%s".formatted(message("order-not-found-with-code"), orderCode))
        );
    }

    @Override
    public Order findByVnpTxnRef(String vnp_TxnRef) {
        return orderRepository.findByVnpTxnRef(vnp_TxnRef).orElseThrow(
                () -> new ResourceNotFoundException(message("order-not-found"))
        );
    }

    @Override
    @Transactional
    public OrderResponse createV2(OrderDTO orderDTO, Principal principal) {
        // validate input data
        validator.validateCreate(orderDTO);

        // retrive data from request
        User user = getUser(principal.getName());
        Address address = getAddress(orderDTO.getAddressId());
        List<OrderItem> orderItems = getOrderItems(orderDTO.getOrderItems());
        EPaymentType paymentType = appUtils.getPaymentType(orderDTO.getPaymentType());
        EOrderStatus status = paymentType.equals(EPaymentType.COD) ? EOrderStatus.ORDERED : EOrderStatus.WAIT_FOR_PAY;

        // build order
        Order order = Order.builder()
                .note(orderDTO.getNote())
                .subTotal(orderDTO.getSubTotal())
                .shippingFee(orderDTO.getShippingFee())
                .total(orderDTO.getTotal())
                .user(user)
                .address(address)
                .orderItems(orderItems)
                .paymentType(paymentType)
                .status(status)
                .totalItems(orderItems.size())
                .isDeleted(false)
                .isPaidBefore(false)
                .build();

        orderItems.forEach(orderItem -> orderItem.setOrder(order));

        // save the order
        Order newOrder = orderRepository.save(order);

        // update skus quantity
        saveChangedSkusQuantity(newOrder);

        return mapEntityToResponse(newOrder);
    }

    private List<OrderItem> getOrderItems(List<OrderItemDTO> itemDTOS) {
        return itemDTOS
                .stream()
                .map(dto -> {
                    OrderItem orderItem = modelMapper.map(dto, OrderItem.class);
                    Product product = productService.findById(dto.getProductId());
                    ProductSku sku = skuService.findById(dto.getSkuId());

                    // Thiết lập SKU & Product cho orderItem
                    orderItem.setId(null);
                    orderItem.setSku(sku);
                    orderItem.setProduct(product);

                    return orderItem;
                })
                .collect(Collectors.toList());
    }


    /**
     * Set order_code value after created ghn_order
     *
     * @param orderId
     * @param orderCode
     */
    @Override
    public void updateOrderCode(Long orderId, String orderCode) {
        Order order = findById(orderId);
        order.setOrderCode(orderCode);
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public OrderResponse createOrder(OrderDTO orderDto, Principal principal) throws JsonProcessingException {
        OrderResponse response = createV2(orderDto, principal); // create order data to duckshop service
        trackingService.afterCreatedOrder(response.getId()); // create tracking order data

        Order order = findById(response.getId());

        // build and create GHN order
        if (order.getPaymentType().equals(EPaymentType.COD)) {
            GhnOrder shippingOrder = ghnService.buildGhnOrder(order);
            String orderCode = ghnService.createGhnOrder(shippingOrder);
            order.setOrderCode(orderCode); // update order code
            orderRepository.save(order);
        }

        cleanUpCartItems(order);

        return mapEntityToResponse(order);
    }

    void cleanUpCartItems(Order order) {
        List<Long> cartItemIds = extractCartItemIds(order);
        cartItemService.deleteListItems(cartItemIds);
        // reupdate cart
        Cart cart = cartService.findByUsername(order.getUser().getUsername());
        cartService.updateCartTotals(cart);
    }

    private List<Long> extractCartItemIds(Order order) {
        List<Long> cartItemIds = order.getOrderItems()
                .stream()
                .map(item -> cartItemService.findByProductIdAndSkuId(item.getProduct().getProductId(), item.getSku().getSkuId()))
                .toList()
                .stream()
                .map(CartItem::getId)
                .distinct() // Remove duplicate IDs (optional)
                .collect(Collectors.toList());
        return cartItemIds;
    }

    @Override
    public OrderResponse create(OrderDTO orderDTO, Principal principal) {
        User user = getUser(principal.getName());
        Address address = getAddress(orderDTO.getAddressId());

        Order order = buildOrder(orderDTO, user, address);
        List<OrderItem> orderItems = convertCartItemIdsToOrderItems(orderDTO.getCartItemIds());

        saveOrder(order, orderItems);

        return mapEntityToResponse(order);
    }

    @Transactional
    @Override
    public OrderResponse createFromCart(OrderDTO orderDTO, Principal principal) {
        String username = principal.getName();

        // retrieve data
        User user = getUser(username);
        Cart cart = cartService.findByUsername(username);
        Address address = getAddress(orderDTO.getAddressId());

        // check items exist in cart
        checkCartItems(cart);

        // set all fields
        Order order = buildOrder(orderDTO, user, address);

        // follow list order items from cart
        List<OrderItem> orderItems = convertCartToOrderItems(cart);
        changeProductNumber(orderItems);

        saveOrder(order, orderItems);

        // delete all items of this cart after order has been created
        clearItems(cart);

        return mapEntityToResponse(order);
    }

    private void changeProductNumber(List<OrderItem> orderItems) {
        orderItems.forEach((item) -> {
            Product product = productService.findById(item.getProduct().getProductId());
            int newSold = item.getQuantity();
//            if (product.getQuantityAvailable() - newSold < 0) {
//                // TODO tạo thông báo lấy thêm hàng cho sản phẩm
//            }
            product.setSold(product.getSold() + newSold);
            product.setQuantityAvailable(product.getQuantityAvailable() - newSold);
            productRepository.save(product);
        });
    }

    @Transactional
    @Override
    public OrderResponse createWithVNPay(OrderDTO orderDTO, String username, String vnp_TxnRef) {
        // retrieve data
        User user = getUser(username);
        Cart cart = cartService.findByUsername(username);
        Address address = getAddress(orderDTO.getAddressId());

        // check items exist in cart
        checkCartItems(cart);

        // build the order
        Order order = buildOrderForVNPayPayment(orderDTO, user, address);
        order.setVnpTxnRef(vnp_TxnRef);

        // follow list order items from cart
        List<OrderItem> orderItems = convertCartToOrderItems(cart);

        saveOrder(order, orderItems);

        // delete all items of this cart after order has been created
        clearItems(cart);

        return mapEntityToResponse(order);
    }

    @Override
    public OrderResponse createWithVNPayV2(Long orderId, String vnp_TxnRef) {
        Order order = findById(orderId);
        order.setVnpTxnRef(vnp_TxnRef);
        return mapEntityToResponse(orderRepository.save(order));
    }

    /**
     * Set true isDeleted Order by orderId (for customers deleted them order)
     *
     * @param orderId
     * @return
     */
    @Override
    public String isDeletedById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(message("order-not-found")));
        order.setIsDeleted(true);
        orderRepository.save(order);
        return message("deleted-successfully");
    }

    /**
     * Admin delete order by orderId
     *
     * @param orderId
     * @return result message
     */
    @Override
    public String deleteById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(message("order-not-found")));
        orderRepository.delete(order);
        return message("deleted-successfully");
    }

    /**
     * Get the order by identifier
     *
     * @param orderId
     * @return
     */
    @Override
    public OrderDetailResponse getById(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() ->
                new ResourceNotFoundException("Order", "id", orderId)
        );
        return mapEntityToDetailResponse(order);
    }

    @Override
    public Order findById(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(
                () -> new ResourceNotFoundException(message("order-not-found"))
        );
    }

    @Override
    public Order findByItemId(Long itemId) {
        return orderRepository.findByItemId(itemId).orElseThrow(() ->
                new ResourceNotFoundException(message("order-not-found"))
        );
    }

    /**
     * Update status for the order
     *
     * @param orderId
     * @param statusValue
     * @return Order Response object
     */
    @Override
    public OrderResponse updateStatus(Long orderId, String statusValue) throws JsonProcessingException {
        Order order = findById(orderId);

        if (order.getStatus().getValue().equals(statusValue)) {
            return mapEntityToResponse(order); // No change in status, return existing order
        }

        EOrderStatus newStatus = appUtils.getOrderStatus(statusValue);

        // Handle cancel `GHN` order specifically
        if (newStatus.equals(EOrderStatus.CANCELED)) {
            handleCancelOrder(order);
        }

        order.setStatus(newStatus);
        orderRepository.save(order);

        return mapEntityToResponse(order);
    }

    private void handleCancelOrder(Order order) throws JsonProcessingException {
        if (order.getOrderCode() != null) {
            String ghnOrderStatus = ghnService.getOrderStatus(order.getOrderCode());
            if (ghnOrderStatus != null) {
                EOrderStatus status = EOrderStatus.fromOrderTrackingStatus(ghnOrderStatus);
                if (status != null) {
                    if (status.equals(EOrderStatus.ORDERED)) {
                        ghnService.cancelGhnOrder(order.getOrderCode());
                    }
                }
            }
        }
        giveBackProductSoldIfCancle(order);
    }

    private void giveBackProductSoldIfCancle(Order order) {
        order.getOrderItems().forEach(item -> {
            Product product = item.getProduct();
            int itemQuantity = item.getQuantity();
            int sold = product.getSold();
            int quantityAvalable = product.getQuantityAvailable();
            product.setSold(sold - itemQuantity);
            product.setQuantityAvailable(quantityAvalable + itemQuantity);
            productRepository.save(product);
        });
    }

    /**
     * Get list order of user with username parameter
     *
     * @param username
     * @return list orderDTO
     */
    @Override
    public List<OrderResponse> getOrdersByUsername(String username) {
        List<Order> orderList = orderRepository.getOrdersByUser_UsernameOrderByCreatedDateDesc(username);

        return orderList
                .stream()
                .map(this::mapEntityToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get list order of user with userId parameter
     *
     * @param userId
     * @return
     */
    @Override
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        List<Order> orderList = orderRepository.getOrdersByUserIdOrderByCreatedDateDesc(userId);

        return orderList
                .stream()
                .map(this::mapEntityToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CheckOutDTO getDataFromUserInfor(Principal principal) {
        String username = principal.getName();

        // retrieve the data from username
        User currentUser = getUser(username);
        Cart userCart = cartService.findByUsername(username);

        // set values
        BigDecimal cartTotal = userCart.getTotalPrice();
        List<AddressDTO> addresses = convertAddressesToAddressesDTO(currentUser.getAddresses());

        CheckOutDTO data = new CheckOutDTO();
        data.setSubtotal(cartTotal);
        data.setAddresses(addresses);

        return data;
    }

    private List<AddressDTO> convertAddressesToAddressesDTO(List<Address> addresses) {
        return addresses
                .stream()
                .map((element) -> modelMapper.map(element, AddressDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getYourOrders(Principal principal) {
        String username = principal.getName();
        List<Order> orderList = orderRepository.findAllByUser_UsernameAndIsDeletedIsFalseOrderByCreatedDateDesc(username);
        return orderList
                .stream()
                .map(this::mapEntityToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void paymentCompleted(String vnp_TxnRef) throws JsonProcessingException {
        Order order = findByVnpTxnRef(vnp_TxnRef);
        log.info(order.getClass().toString());

        // build and create GHN order
        GhnOrder shippingOrder = ghnService.buildGhnOrder(order);
        String orderCode = ghnService.createGhnOrder(shippingOrder);

        order.setStatus(EOrderStatus.ORDERED);
        order.setIsPaidBefore(true);
        order.setOrderCode(orderCode); // update order code
        order.setCreatedDate(DateTimeConfig.getCurrentDateTimeInTimeZone());
        orderRepository.save(order); // save change
    }

    @Override
    public OrderPageResponse adminFilter(String statusValue, String key, List<String> sortCriteria, int pageNo, int pageSize) {
        try {
            // check and retrive data caching 👇
            logger.info(String.format("user filter: status=%s, key=%s, sort=%s, page_no=%d, page_size=%d", statusValue, key, sortCriteria, pageNo, pageSize));
            String redisKey = redisService.getKeyFrom(AppUtils.KEY_PREFIX_GET_ALL_ORDER, statusValue, key, sortCriteria, pageNo, pageSize);
            OrderPageResponse response = redisService.getAll(redisKey, OrderPageResponse.class);
            if (response != null && !response.getContent().isEmpty()) {
                return response;
            }

            // follow Pageable instances
            Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
            EOrderStatus status = null;
            if (statusValue != null) {
                status = appUtils.getOrderStatus(statusValue);
            }
            Page<Order> orderPage = orderRepository.filter(status, key, sortCriteria, pageable);

            // get content for page object
            List<Order> orderList = orderPage.getContent();
            List<OrderResponse> content = orderList.stream()
                    .map(this::mapEntityToResponse)
                    .collect(Collectors.toList());

            // set data to the product response
            OrderPageResponse pageResponse = new OrderPageResponse();
            pageResponse.setContent(content);
            pageResponse.setPageNo(orderPage.getNumber() + 1);
            pageResponse.setPageSize(orderPage.getSize());
            pageResponse.setTotalPages(orderPage.getTotalPages());
            pageResponse.setTotalElements(orderPage.getTotalElements());
            pageResponse.setLast(orderPage.isLast());

            redisService.saveAll(redisKey, pageResponse); //👈 save caching data
            return pageResponse;
        } catch (Exception e) {
            throw new InvalidException(message("list-order-is-empty"));
        }
    }

    @Override
    public OrderPageResponse userFilter(String statusValue, String key, int pageNo, int pageSize, Principal principal) {
        try {
            // check and retrive data caching 👇
            String username = principal.getName();
            logger.info(String.format("user filter: username=%s, status=%s, key=%s, page_no=%d, page_size=%d", username, statusValue, key, pageNo, pageSize));
            String keyPrefix = username != null ? (AppUtils.KEY_PREFIX_GET_ALL_ORDER + ":" + username) : (AppUtils.KEY_PREFIX_GET_ALL_ORDER);
            String redisKey = redisService.getKeyFrom(keyPrefix, statusValue, key, pageNo, pageSize);
            OrderPageResponse response = redisService.getAll(redisKey, OrderPageResponse.class);
            if (response != null && !response.getContent().isEmpty()) {
                return response;
            }

            // data caching not exists 👇
            Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
            EOrderStatus status = null;
            if (statusValue != null) {
                status = appUtils.getOrderStatus(statusValue);
            }
            Page<Order> orderPage = orderRepository.userFilter(status, key, principal.getName(), pageable);
            // get content for page object
            List<Order> orderList = orderPage.getContent();
            List<OrderResponse> content = orderList.stream()
                    .map(this::mapEntityToResponse)
                    .collect(Collectors.toList());

            // set data to the product response 👇
            OrderPageResponse pageResponse = new OrderPageResponse();
            pageResponse.setContent(content);
            pageResponse.setPageNo(orderPage.getNumber() + 1);
            pageResponse.setPageSize(orderPage.getSize());
            pageResponse.setTotalPages(orderPage.getTotalPages());
            pageResponse.setTotalElements(orderPage.getTotalElements());
            pageResponse.setLast(orderPage.isLast());

            redisService.saveAll(redisKey, pageResponse); //👈 save caching data
            return pageResponse;
        } catch (Exception e) {
            throw new InvalidException(message("list-order-is-empty"));
        }
    }

    @Override
    public OrderResponse makePaymentForCOD(OrderDTO dto, Long orderId) throws JsonProcessingException {
        Order order = findById(orderId);
        Address address = getAddress(dto.getAddressId());

        // set fields
        BigDecimal shippingFee = BigDecimal.valueOf(order.getTotal().longValue() - order.getSubTotal().longValue());
        ZonedDateTime now = DateTimeConfig.getCurrentDateTimeInTimeZone();
        order.setCreatedDate(now);
        order.setShippingFee(shippingFee);
        order.setTotal(dto.getTotal());
        order.setNote(dto.getNote());
        order.setAddress(address);
        order.setStatus(EOrderStatus.ORDERED);
        order.setPaymentType(EPaymentType.COD);

        // build and create GHN order
        GhnOrder shippingOrder = ghnService.buildGhnOrder(order);
        String orderCode = ghnService.createGhnOrder(shippingOrder);
        order.setOrderCode(orderCode);
        return mapEntityToResponse(orderRepository.save(order)); // save change
    }

    @Override
    public void makePaymentForVNPAY(OrderDTO dto) {
        // retrieve data
        Order order = findById(dto.getId());
        Address address = getAddress(dto.getAddressId());

        // set fields
        order.setTotal(dto.getTotal());
        order.setNote(dto.getNote());
        order.setAddress(address);
        order.setVnpTxnRef(dto.getId().toString());

        // save the order
        orderRepository.save(order);
    }

    @Override
    public List<OrderResponse> findYourOrderByStatus(String value, Principal principal) {
        String username = principal.getName();
        List<Order> orderList;

        if (value == null) {
            orderList = orderRepository
                    .getOrdersByUser_UsernameOrderByCreatedDateDesc(username);
        } else {
            EOrderStatus status = appUtils.getOrderStatus(value);
            orderList = orderRepository
                    .findAllByStatusAndUser_UsernameAndIsDeletedIsFalseOrderByCreatedDateDesc(status, username);
        }

        return orderList.stream()
                .map(this::mapEntityToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    protected void clearItems(Cart cart) {
        if (cart.getCartItems().size() > 0) {
            try {
                cart.getCartItems().forEach(
                        (item) -> cartItemRepository.deleteById(item.getId())
                );
            } catch (Exception e) {
                e.printStackTrace();
            }

            cart.getCartItems().clear();
            cartService.updateCartTotals(cart);
        }
    }

    private Address getAddress(Long addressId) {
        return addressService.findById(addressId);
    }

    private User getUser(String username) {
        return userService.findByUsername(username);
    }

    public Order buildOrder(OrderDTO orderDTO, User user, Address address) {
        Order order = new Order();
        order.setStatus(EOrderStatus.ORDERED);
        order.setIsDeleted(false);
        order.setIsPaidBefore(false);
        order.setNote(orderDTO.getNote());
        order.setTotal(orderDTO.getTotal());
        order.setUser(user);
        order.setAddress(address);

        // retrieve enum payment type from input
        EPaymentType paymentType = appUtils.getPaymentType(orderDTO.getPaymentType());
        order.setPaymentType(paymentType);

        return order;
    }

    public Order buildOrderForVNPayPayment(OrderDTO orderDTO, User user, Address address) {
        Order order = new Order();
        order.setStatus(EOrderStatus.WAIT_FOR_PAY);
        order.setIsDeleted(false);
        order.setIsPaidBefore(false);
        order.setNote(orderDTO.getNote());
        order.setTotal(orderDTO.getTotal());
        order.setUser(user);
        order.setAddress(address);
        order.setPaymentType(EPaymentType.VN_PAY);

        return order;
    }

    private void saveOrder(Order order, List<OrderItem> orderItems) {
        order.setOrderItems(orderItems);
        order.setTotalItems(orderItems.size());

        orderItems.forEach(orderItem -> orderItem.setOrder(order));
        orderRepository.save(order);
    }

    private void checkCartItems(Cart cart) {
        if (cart.getCartItems().isEmpty()) {
            throw new APIException(message("cart-is-empty"));
        }
    }

    private List<OrderItem> convertCartToOrderItems(Cart cart) {
        return cart.getCartItems()
                .stream()
                .map(cartItem -> {
                    OrderItem orderItem = modelMapper.map(cartItem, OrderItem.class);
                    orderItem.setId(null);

                    // Thiết lập SKU & Product cho orderItem
                    orderItem.setSku(cartItem.getSku());
                    orderItem.setProduct(cartItem.getProduct());

                    return orderItem;
                })
                .collect(Collectors.toList());
    }

    private List<OrderItem> convertCartItemIdsToOrderItems(List<Long> cartItemIds) {
        return cartItemIds
                .stream()
                .map(idCartItem -> {
                    CartItem cartItem = cartItemService.findById(idCartItem);

                    OrderItem orderItem = modelMapper.map(cartItem, OrderItem.class);
                    orderItem.setId(null);

                    // Thiết lập SKU & Product cho orderItem
                    orderItem.setSku(cartItem.getSku());
                    orderItem.setProduct(cartItem.getProduct());

                    return orderItem;
                })
                .collect(Collectors.toList());
    }

    private OrderResponse mapEntityToResponse(Order order) {
        OrderResponse response = modelMapper.map(order, OrderResponse.class);
        response.setStatus(order.getStatus().getValue());
        response.setPaymentType(order.getPaymentType().getValue());
        response.getOrderItems().forEach(item -> item.setHasReview(
                reviewRepository.checkHasReview(item.getId()))
        );
        return response;
    }

    private OrderDetailResponse mapEntityToDetailResponse(Order order) {
        OrderDetailResponse response = modelMapper.map(order, OrderDetailResponse.class);
        response.setStatus(order.getStatus().getValue());
        response.setPaymentType(order.getPaymentType().getValue());
        response.getOrderItems().forEach(item -> item.setHasReview(
                reviewRepository.checkHasReview(item.getId()))
        );
        List<OrderTrackingDTO> trackingDTOS = trackingService.getAll(order.getId());
        response.setTrackingDTOs(trackingDTOS);
        return response;
    }

    private String message(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}


