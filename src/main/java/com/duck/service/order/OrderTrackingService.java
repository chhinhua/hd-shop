package com.duck.service.order;

import com.duck.dto.order.OrderTrackingDTO;

public interface OrderTrackingService {
    void create(final OrderTrackingDTO dto);

    void afterCreatedOrder(final Long orderId);
}
