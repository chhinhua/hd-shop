package com.duck.service.order;

import com.duck.dto.order.OrderTrackingDTO;

import java.util.List;

public interface OrderTrackingService {
    void create(final OrderTrackingDTO dto);

    void afterCreatedOrder(final Long orderId);

    List<OrderTrackingDTO> getAll(final Long orderId);
}
