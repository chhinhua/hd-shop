package com.hdshop.service.order;

import com.hdshop.dto.order.OrderDTO;

import java.util.List;

public interface OrderService {
    OrderDTO createOrder(final OrderDTO order);

    void deleteOrderById(final Long orderId);

    OrderDTO getOrderById(final Long orderId);

    OrderDTO updateStatus(final Long orderId, final String status);

    List<OrderDTO> getOrdersByUsername(final String username);

    List<OrderDTO> getOrdersByUserId(final Long userId);
}
