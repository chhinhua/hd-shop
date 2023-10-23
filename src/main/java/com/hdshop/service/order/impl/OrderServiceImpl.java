package com.hdshop.service.order.impl;

import com.hdshop.dto.order.OrderDTO;
import com.hdshop.entity.Address;
import com.hdshop.entity.Category;
import com.hdshop.entity.Order;
import com.hdshop.entity.User;
import com.hdshop.exception.ResourceNotFoundException;
import com.hdshop.repository.AddressRepository;
import com.hdshop.repository.OrderRepository;
import com.hdshop.repository.UserRepository;
import com.hdshop.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;

    /**
     * Create a new Order
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
        //TODO Set status for order base on status value from frontend

        // save order
        Order newOrder = orderRepository.save(order);

        return mapToDTO(newOrder);
    }

    /**
     * Delete Order by orderId
     * @param orderId
     */
    @Override
    public void deleteOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        orderRepository.delete(order);
    }

    private Order mapToEntity(OrderDTO orderDTO) {
        return modelMapper.map(orderDTO, Order.class);
    }

    private OrderDTO mapToDTO(Order order) {
        return modelMapper.map(order, OrderDTO.class);
    }
}
