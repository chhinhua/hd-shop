package com.hdshop.service.order.impl;

import com.hdshop.dto.order.OrderDTO;
import com.hdshop.dto.order.OrderResponse;
import com.hdshop.entity.*;
import com.hdshop.exception.APIException;
import com.hdshop.exception.ResourceNotFoundException;
import com.hdshop.repository.*;
import com.hdshop.service.order.OrderService;
import com.hdshop.utils.EnumOrderStatus;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

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

    @Override
    public OrderResponse addOrderFromUserCart(OrderDTO orderDTO, Principal principal) {
        String username = principal.getName();

        // retrieve data
        User user = getUserByUsername(username);
        Cart cart = getCartByUsername(username);
        Address address = getAddressById(orderDTO.getAddressId());

        // check items exist in cart
        checkCartItems(cart);

        // set all fields
        Order order = buildOrder(orderDTO, user, address);

        // create list order items from cart
        List<OrderItem> orderItems = convertCartToOrderItems(cart);

        saveOrder(order, orderItems);

        // delete all items of this cart after order has been created
        clearItems(cart);

        return mapEntityToResponse(order);
    }

    /**
     * Delete Order by orderId
     *
     * @param orderId
     */
    @Override
    public void deleteOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        orderRepository.delete(order);
    }

    /**
     * Get the order by identifier
     *
     * @param orderId
     * @return
     */
    @Override
    public OrderDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        OrderDTO orderDTO = mapEntityToDTO(order);
        orderDTO.setStatus(order.getStatus().getValue());

        return orderDTO;
    }

    /**
     * Update status for the order
     *
     * @param orderId
     * @param statusKey
     * @return OrderDTO
     */
    @Override
    public OrderDTO updateStatus(Long orderId, String statusKey) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        // thay đổi nếu trạng thái khác trạng thái hiện tại
        String orderStatusKey = order.getStatus().getKey();
        if (!orderStatusKey.equals(statusKey)) {
            EnumOrderStatus newStatus = EnumOrderStatus.valueOf(statusKey.toUpperCase());
            order.setStatus(newStatus);
            orderRepository.save(order);
        }

        return mapEntityToDTO(order);
    }

    /**
     * Get list order of user with username parameter
     *
     * @param username
     * @return list orderDTO
     */
    @Override
    public List<OrderDTO> getOrdersByUsername(String username) {
        List<Order> orderList = orderRepository.getOrdersByUser_UsernameOrderByCreatedDateDesc(username);

        return orderList
                .stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get list order of user with userId parameter
     *
     * @param userId
     * @return
     */
    @Override
    public List<OrderDTO> getOrdersByUserId(Long userId) {
        List<Order> orderList = orderRepository.getOrdersByUserIdOrderByCreatedDateDesc(userId);

        return orderList
                .stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    private void clearItems(Cart cart) {
        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear();
    }

    private Cart getCartByUsername(String username) {
        return cartRepository.findByUser_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException(getMessage("cart-not-found")));
    }

    private Address getAddressById(Long addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException(getMessage("no-delivery-address-found")));
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(getMessage("user-not-found")));
    }

    private void checkCartItems(Cart cart) {
        if (cart.getCartItems().isEmpty()) {
            throw new APIException(getMessage("cart-is-empty"));
        }
    }

    public Order buildOrder(OrderDTO orderDTO, User user, Address address) {
        Order order = new Order();
        order.setStatus(EnumOrderStatus.ORDERED);
        order.setIsPaidBefore(false);
        order.setNote(orderDTO.getNote());
        order.setPaymentType(orderDTO.getPaymentType());
        order.setTotal(orderDTO.getTotal());
        order.setUser(user);
        order.setAddress(address);
        return order;
    }

    private void saveOrder(Order order, List<OrderItem> orderItems) {
        order.setOrderItems(orderItems);
        order.setTotalItems(orderItems.size());

        orderItems.forEach(orderItem -> orderItem.setOrder(order));

        orderRepository.save(order);
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
        return response;
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}


