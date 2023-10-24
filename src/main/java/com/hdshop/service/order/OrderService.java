package com.hdshop.service.order;

import com.hdshop.dto.order.OrderDTO;

public interface OrderService {
    OrderDTO createOrder(final OrderDTO order);

    void deleteOrderById(final Long orderId);

    OrderDTO getOrderById(final Long orderId);

    OrderDTO updateStatus(final Long orderId, final String status);
}
