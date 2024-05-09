package com.hdshop.service.order.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hdshop.config.VNPayConfig;
import com.hdshop.dto.address.AddressDTO;
import com.hdshop.dto.ghn.GhnOrder;
import com.hdshop.dto.order.*;
import com.hdshop.entity.*;
import com.hdshop.exception.APIException;
import com.hdshop.exception.InvalidException;
import com.hdshop.exception.ResourceNotFoundException;
import com.hdshop.repository.*;
import com.hdshop.service.address.AddressService;
import com.hdshop.service.cart.CartItemService;
import com.hdshop.service.cart.CartService;
import com.hdshop.service.ghn.GhnService;
import com.hdshop.service.order.OrderService;
import com.hdshop.service.product.ProductService;
import com.hdshop.service.product.ProductSkuService;
import com.hdshop.service.user.UserService;
import com.hdshop.utils.AppUtils;
import com.hdshop.utils.EnumOrderStatus;
import com.hdshop.utils.EnumPaymentType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderServiceImpl implements OrderService {
    AddressService addressService;
    AppUtils appUtils;
    CartItemRepository cartItemRepository;
    CartRepository cartRepository;
    CartService cartService;
    CartItemService cartItemService;
    GhnService ghnService;
    MessageSource messageSource;
    ModelMapper modelMapper;
    OrderRepository orderRepository;
    UserService userService;
    ProductService productService;
    ProductRepository productRepository;
    ReviewRepository reviewRepository;
    ProductSkuService skuService;

    @Override
    public OrderResponse createV2(OrderDTO orderDTO, Principal principal) {
        // retrive data from request
        User user = getUser(principal.getName());
        Address address = getAddress(orderDTO.getAddressId());
        List<OrderItem> orderItems = getOrderItems(orderDTO.getOrderItems());
        EnumPaymentType paymentType = appUtils.getPaymentType(orderDTO.getPaymentType());

        // build order
        Order order = Order.builder()
                .status(EnumOrderStatus.ORDERED)
                .isDeleted(false)
                .isPaidBefore(false)
                .note(orderDTO.getNote())
                .subTotal(orderDTO.getSubTotal())
                .shippingFee(orderDTO.getShippingFee())
                .total(orderDTO.getTotal())
                .user(user)
                .address(address)
                .orderItems(orderItems)
                .paymentType(paymentType)
                .totalItems(orderItems.size())
                .build();

        orderItems.forEach(orderItem -> orderItem.setOrder(order));

        // save the order
        Order newOrder = orderRepository.save(order);
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
    public OrderResponse createOrder(OrderDTO orderDto, Principal principal) throws JsonProcessingException {
        OrderResponse response = createFromCart(orderDto, principal); // create order data to duckshop service
        Order order = findById(response.getId());

        GhnOrder shippingOrder = ghnService.buildGhnOrder(order);
        String orderCode = ghnService.createGhnOrder(shippingOrder); // create order data to GHN service

        updateOrderCode(response.getId(), orderCode);

        return response;
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
        Cart cart = getCartByUsername(username);
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
        Cart cart = getCartByUsername(username);
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

    /**
     * Set true isDeleted Order by orderId (for customers deleted them order)
     *
     * @param orderId
     * @return
     */
    @Override
    public String isDeletedById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(getMessage("order-not-found")));
        order.setIsDeleted(true);
        orderRepository.save(order);
        return getMessage("deleted-successfully");
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
                .orElseThrow(() -> new ResourceNotFoundException(getMessage("order-not-found")));
        orderRepository.delete(order);
        return getMessage("deleted-successfully");
    }

    /**
     * Get the order by identifier
     *
     * @param orderId
     * @return
     */
    @Override
    public OrderResponse getById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        OrderDTO orderDTO = mapEntityToDTO(order);
        orderDTO.setStatus(order.getStatus().getValue());

        return mapEntityToResponse(order);
    }

    @Override
    public Order findById(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(
                () -> new ResourceNotFoundException(getMessage("order-not-found"))
        );
    }

    @Override
    public Order findByItemId(Long itemId) {
        return orderRepository.findByItemId(itemId).orElseThrow(() ->
                new ResourceNotFoundException(getMessage("order-not-found"))
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

        EnumOrderStatus newStatus = appUtils.getOrderStatus(statusValue);

        // Handle cancel `GHN` order specifically
        if (newStatus.equals(EnumOrderStatus.CANCELED)) {
            handleCancelOrder(order);
        }

        order.setStatus(newStatus);
        orderRepository.save(order);

        return mapEntityToResponse(order);
    }

    private void  handleCancelOrder(Order order) throws JsonProcessingException {
        if (order.getOrderCode() != null) {
            String ghnOrderStatus = ghnService.getOrderStatus(order.getOrderCode());
            if (ghnOrderStatus != null) {
                EnumOrderStatus status = ghnService.getEnumStatus(ghnOrderStatus);
                if (status != null) {
                    if (status.equals(EnumOrderStatus.ORDERED)) {
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
        Cart userCart = getCartByUsername(username);

        BigDecimal cartTotal = userCart.getTotalPrice();

        // set values
        CheckOutDTO data = new CheckOutDTO();

        List<AddressDTO> addresses = convertAddressesToAddressesDTO(currentUser.getAddresses());
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
    public void paymentCompleted(String vnp_TxnRef) {
        // TODO must optimize code here
        Order order = orderRepository.findByVnpTxnRef(vnp_TxnRef)
                .orElseThrow(() -> new ResourceNotFoundException(getMessage("order-not-found")));

        // update status
        order.setStatus(EnumOrderStatus.ORDERED);
        order.setIsPaidBefore(true);
        orderRepository.save(order);

        // clear cart items
        Cart cart = cartRepository.findByUser_Username(order.getUser().getUsername())
                .orElseThrow(() -> new ResourceNotFoundException(getMessage("cart-not-found")));
        clearItems(cart);
    }

    @Override
    public OrderPageResponse adminFilter(String statusValue, String key, List<String> sortCriteria, int pageNo, int pageSize) {
        try {
            // follow Pageable instances
            Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

            EnumOrderStatus status = null;
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

            return pageResponse;
        } catch (Exception e) {
            throw new InvalidException(getMessage("list-order-is-empty"));
        }
    }

    @Override
    public OrderPageResponse userFilter(String statusValue, String key, int pageNo, int pageSize, Principal principal) {
        try {
            // follow Pageable instances
            Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

            EnumOrderStatus status = null;
            if (statusValue != null) {
                status = appUtils.getOrderStatus(statusValue);
            }

            Page<Order> orderPage = orderRepository.userFilter(status, key, principal.getName(), pageable);

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

            return pageResponse;
        } catch (Exception e) {
            throw new InvalidException(getMessage("list-order-is-empty"));
        }
    }

    @Override
    public OrderResponse makePaymentForCOD(OrderDTO dto, Long orderId) {
        // TODO must test & debug
        Order order = findById(orderId);
        Address newAddress = getAddress(dto.getAddressId());

        // set fields
        order.setTotal(dto.getTotal());
        order.setNote(dto.getNote());
        order.setAddress(newAddress);
        order.setStatus(EnumOrderStatus.ORDERED);
        order.setPaymentType(EnumPaymentType.COD);

        Order makePayment = orderRepository.save(order);
        return mapEntityToResponse(makePayment);
    }

    @Override
    public void makePaymentForVNPAY(OrderDTO dto, Long orderId) {
        // TODO must test & debug
        // retrieve data
        Order order = findById(orderId);
        Address newAddress = getAddress(dto.getAddressId());

        // set fields
        order.setTotal(dto.getTotal());
        order.setNote(dto.getNote());
        order.setAddress(newAddress);
        order.setVnpTxnRef(VNPayConfig.vnp_TxnRef);

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
            EnumOrderStatus status = appUtils.getOrderStatus(value);
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

    private Cart getCartByUsername(String username) {
        return cartRepository.findByUser_Username(username).orElseThrow(() ->
                new ResourceNotFoundException(getMessage("cart-not-found"))
        );
    }

    private Address getAddress(Long addressId) {
        return addressService.findById(addressId);
    }

    private User getUser(String username) {
        return userService.findByUsername(username);
    }

    public Order buildOrder(OrderDTO orderDTO, User user, Address address) {
        Order order = new Order();
        order.setStatus(EnumOrderStatus.ORDERED);
        order.setIsDeleted(false);
        order.setIsPaidBefore(false);
        order.setNote(orderDTO.getNote());
        order.setTotal(orderDTO.getTotal());
        order.setUser(user);
        order.setAddress(address);

        // retrieve enum payment type from input
        EnumPaymentType paymentType = appUtils.getPaymentType(orderDTO.getPaymentType());
        order.setPaymentType(paymentType);

        return order;
    }

    public Order buildOrderForVNPayPayment(OrderDTO orderDTO, User user, Address address) {
        Order order = new Order();
        order.setStatus(EnumOrderStatus.WAIT_FOR_PAY);
        order.setIsDeleted(false);
        order.setIsPaidBefore(false);
        order.setNote(orderDTO.getNote());
        order.setTotal(orderDTO.getTotal());
        order.setUser(user);
        order.setAddress(address);
        order.setPaymentType(EnumPaymentType.VN_PAY);

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
            throw new APIException(getMessage("cart-is-empty"));
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

    private OrderDTO mapEntityToDTO(Order order) {
        OrderDTO dto = modelMapper.map(order, OrderDTO.class);
        dto.setStatus(order.getStatus().getValue());
        return dto;
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

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}


