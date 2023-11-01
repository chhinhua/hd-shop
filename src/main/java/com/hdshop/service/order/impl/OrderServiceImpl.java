package com.hdshop.service.order.impl;

import com.hdshop.dto.order.OrderDTO;
import com.hdshop.entity.Address;
import com.hdshop.entity.Order;
import com.hdshop.entity.User;
import com.hdshop.exception.ResourceNotFoundException;
import com.hdshop.repository.AddressRepository;
import com.hdshop.repository.order.OrderRepository;
import com.hdshop.repository.user.UserRepository;
import com.hdshop.service.order.OrderService;
import com.hdshop.utils.EnumOrderStatus;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;

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
        Address address = addressRepository.findById(orderDTO.getAddress().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", orderDTO.getAddress().getId()));

        Order order = mapToEntity(orderDTO);
        order.setUser(user);
        order.setAddress(address);

        // save order
        Order newOrder = orderRepository.save(order);

        return mapToDTO(newOrder);
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

        String statusValue = convertKeyToValue(order.getStatus());

        OrderDTO orderDTO = mapToDTO(order);
        orderDTO.setStatus(statusValue);

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
        if (!order.getStatus().equals(statusKey)) {
            for (EnumOrderStatus status : EnumOrderStatus.values()) {
                if (status.getKey().toLowerCase().equals(statusKey.toLowerCase())) {
                    order.setStatus(status.getKey());
                    orderRepository.save(order);
                }
            }
        }

        OrderDTO orderDTO = mapToDTO(order);
        orderDTO.setStatus(convertKeyToValue(order.getStatus()));

        return orderDTO;
    }

    /**
     * Get list order of user with username parameter
     * @param username
     * @return list orderDTO
     */
    @Override
    public List<OrderDTO> getOrdersByUsername(String username) {
        List<Order> orderList = orderRepository.getOrdersByUser_UsernameOrderByCreatedDateDesc(username);

        return orderList
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get list order of user with userId parameter
     * @param userId
     * @return
     */
    @Override
    public List<OrderDTO> getOrdersByUserId(Long userId) {
        List<Order> orderList = orderRepository.getOrdersByUserIdOrderByCreatedDateDesc(userId);

        return orderList
                .stream()
                .map(this::mapToDTO)
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

    private OrderDTO mapToDTO(Order order) {
        return modelMapper.map(order, OrderDTO.class);
    }
}
