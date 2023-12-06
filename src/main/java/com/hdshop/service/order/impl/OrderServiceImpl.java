package com.hdshop.service.order.impl;

import com.hdshop.config.VNPayConfig;
import com.hdshop.dto.address.AddressDTO;
import com.hdshop.dto.order.CheckOutDTO;
import com.hdshop.dto.order.OrderDTO;
import com.hdshop.dto.order.OrderPageResponse;
import com.hdshop.dto.order.OrderResponse;
import com.hdshop.entity.*;
import com.hdshop.exception.APIException;
import com.hdshop.exception.InvalidException;
import com.hdshop.exception.ResourceNotFoundException;
import com.hdshop.repository.*;
import com.hdshop.service.cart.CartService;
import com.hdshop.service.order.OrderService;
import com.hdshop.service.product.ProductService;
import com.hdshop.utils.AppUtils;
import com.hdshop.utils.EnumOrderStatus;
import com.hdshop.utils.EnumPaymentType;
import lombok.RequiredArgsConstructor;
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
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;
    private final MessageSource messageSource;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final CartService cartService;
    private final ProductService productService;
    private final AppUtils appUtils;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public OrderResponse addOrder(OrderDTO orderDTO, Principal principal) {
        String username = principal.getName();

        User user = getUserByUsername(username);
        Address address = getAddressById(orderDTO.getAddressId());

        Order order = buildOrder(orderDTO, user, address);
        List<OrderItem> orderItems = convertCartItemIdsToOrderItems(orderDTO.getCartItemIds());

        saveOrder(order, orderItems);

        return mapEntityToResponse(order);
    }

    @Transactional
    @Override
    public OrderResponse createOrderFromUserCart(OrderDTO orderDTO, Principal principal) {
        String username = principal.getName();

        // retrieve data
        User user = getUserByUsername(username);
        Cart cart = getCartByUsername(username);
        Address address = getAddressById(orderDTO.getAddressId());

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
    public OrderResponse createOrderWithVNPay(OrderDTO orderDTO, String username, String vnp_TxnRef) {
        // retrieve data
        User user = getUserByUsername(username);
        Cart cart = getCartByUsername(username);
        Address address = getAddressById(orderDTO.getAddressId());

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
    public String isDeletedOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(getMessage("order-not-found")));
        order.setIsDeleted(true);
        orderRepository.save(order);
        return getMessage("deleted-successfully");
    }

    /**
     * Admin delete order by orderId
     * @param orderId
     * @return result message
     */
    @Override
    public String deleteOrderById(Long orderId) {
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
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        OrderDTO orderDTO = mapEntityToDTO(order);
        orderDTO.setStatus(order.getStatus().getValue());

        return mapEntityToResponse(order);
    }

    @Override
    public Order findById(Long orderId) {
        return orderRepository
                .findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(getMessage("order-not-found")));
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
     * @return OrderDTO
     */
    @Override
    public OrderResponse updateStatus(Long orderId, String statusValue) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        // thay đổi nếu trạng thái khác trạng thái hiện tại
        String orderStatusKey = order.getStatus().getValue();
        if (!orderStatusKey.equals(statusValue)) {
            EnumOrderStatus newStatus = appUtils.getOrderStatus(statusValue);
            order.setStatus(newStatus);
            orderRepository.save(order);

            // hoàn trả lại số lượng sản phẩm nếu hủy đơn
            giveBackProductSoldIfCancle(order, newStatus);
        }

        return mapEntityToResponse(order);
    }

    private void giveBackProductSoldIfCancle(Order order, EnumOrderStatus status) {
        if (status == EnumOrderStatus.CANCELED) {
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
        User currentUser = getUserByUsername(username);
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
    public List<OrderResponse> getListOrderByCurrentUser(Principal principal) {
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
    public void paymentFailed(String vnp_TxnRef) {
        Order order = orderRepository.findByVnpTxnRef(vnp_TxnRef)
                .orElseThrow(() -> new ResourceNotFoundException(getMessage("order-not-found")));
        orderRepository.delete(order);
    }

    @Override
    public OrderPageResponse filter(String statusValue, String key, List<String> sortCriteria, int pageNo, int pageSize) {
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
    public OrderResponse makePaymentForCOD(OrderDTO dto, Long orderId) {
        // TODO must test & debug
        Order order = findById(orderId);
        Address newAddress = getAddressById(dto.getAddressId());

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
        Address newAddress = getAddressById(dto.getAddressId());

        // set fields
        order.setTotal(dto.getTotal());
        order.setNote(dto.getNote());
        order.setAddress(newAddress);
        order.setVnpTxnRef(VNPayConfig.vnp_TxnRef);

        // save the order
        orderRepository.save(order);
    }

    @Override
    public OrderPageResponse getAllOrders(int pageNo, int pageSize) {
        // follow Pageable instances
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

        Page<Order> oderPage = orderRepository.findAll(pageable);

        // get content for page object
        List<Order> orderList = oderPage.getContent();

        List<OrderResponse> content = orderList.stream()
                .map(this::mapEntityToResponse)
                .collect(Collectors.toList());

        // set data to the order response
        OrderPageResponse orderResponse = new OrderPageResponse();
        orderResponse.setContent(content);
        orderResponse.setPageNo(oderPage.getNumber() + 1);
        orderResponse.setPageSize(oderPage.getSize());
        orderResponse.setTotalPages(oderPage.getTotalPages());
        orderResponse.setTotalElements(oderPage.getTotalElements());
        orderResponse.setLast(oderPage.isLast());

        return orderResponse;
    }

    @Override
    public List<OrderResponse> findForUserByStatus(String value, Principal principal) {
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

    @Override
    public List<OrderResponse> findByStatus(String value) {
        List<Order> orderList;

        if (value == null) {
            orderList = orderRepository.findAll();
        } else {
            EnumOrderStatus status = appUtils.getOrderStatus(value);
            orderList = orderRepository.findByStatusOrderByCreatedDate(status);
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

    private Address getAddressById(Long addressId) {
        return addressRepository.findById(addressId).orElseThrow(() ->
                new ResourceNotFoundException(getMessage("no-delivery-address-found"))
        );
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() ->
                new ResourceNotFoundException(getMessage("user-not-found"))
        );
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
                    CartItem cartItem = cartItemRepository.findById(idCartItem)
                            .orElseThrow(() -> new ResourceNotFoundException(getMessage("cart-item-not-found")));

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


