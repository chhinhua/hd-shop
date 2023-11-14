package com.hdshop.service.order.impl;

import com.hdshop.dto.order.OrderDTO;
import com.hdshop.dto.order.OrderItemDTO;
import com.hdshop.dto.order.OrderResponse;
import com.hdshop.entity.Address;
import com.hdshop.entity.Order;
import com.hdshop.entity.OrderItem;
import com.hdshop.entity.User;
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
    private final ProductSkuRepository skuRepository;
    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;
    private final MessageSource messageSource;

    /**
     * Create a new Order
     *
     * @param orderDTO
     * @return orderDTO object have been created
     */
    @Override
    public OrderDTO createOrder(OrderDTO orderDTO) {
        // Check existing user
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", orderDTO.getUserId()));

        // Check existing address
        Address address = addressRepository.findById(orderDTO.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", orderDTO.getAddressId()));

        Order order = mapToEntity(orderDTO);
        order.setUser(user);
        order.setAddress(address);

        // save order
        Order newOrder = orderRepository.save(order);

        return mapEntityToDTO(newOrder);
    }

    @Override
    public OrderResponse addOrder(OrderDTO orderDTO, Principal principal) {
        String username = principal.getName();

        // Check existing user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(getMessage("user-not-found")));

        // Check existing address
        Address address = addressRepository.findById(orderDTO.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", orderDTO.getAddressId()));

        // set fields
        Order order = new Order();
        order.setStatus(EnumOrderStatus.ORDERED);
        order.setIsPaidBefore(false);
        order.setNote(orderDTO.getNote());
        order.setPaymentType(orderDTO.getPaymentType());
        order.setTotal(orderDTO.getTotal());
        order.setUser(user);
        order.setAddress(address);

        // set order for orderItems
        List<OrderItem> orderItems = convertItemDTOsToItemsEntity(orderDTO.getOrderItems());
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrder(order); // Set the order for each OrderItem
        }
        order.setOrderItems(orderItems);

        // save order
        Order newOrder = orderRepository.save(order);

        return mapEntityToResponse(newOrder);
    }

    private List<OrderItem> convertItemDTOsToItemsEntity(List<OrderItemDTO> itemDTOS) {
        return itemDTOS
                .stream()
                .map(itemDTO -> {
                    OrderItem orderItem = modelMapper.map(itemDTO, OrderItem.class);

                    // Thiết lập SKU thủ công dựa trên skuId từ DTO
                    orderItem.setSku(skuRepository.findById(itemDTO.getSkuId()).orElse(null));

                    // Thiết lập Product thủ công dựa trên productId từ DTO
                    orderItem.setProduct(productRepository.findById(itemDTO.getProductId()).orElseThrow(()
                            -> new ResourceNotFoundException(getMessage("product-not-found-with-id-is") + itemDTO.getProductId())
                    ));

                    return orderItem;
                })
                .collect(Collectors.toList());
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

    public String convertKeyToValue(String key) {
        for (EnumOrderStatus status : EnumOrderStatus.values()) {
            if (status.getKey().equals(key)) {
                return status.getValue();
            }
        }
        return null;
    }


    private Order mapToEntity(OrderDTO orderDTO) {
        return modelMapper.map(orderDTO, Order.class);
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


