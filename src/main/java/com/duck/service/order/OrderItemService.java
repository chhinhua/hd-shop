package com.duck.service.order;

import com.duck.dto.order.OrderItemDTO;
import com.duck.entity.OrderItem;

public interface OrderItemService {
    OrderItem findById(final Long orderItemId);

    OrderItem mapToOrderItem(final OrderItemDTO itemDTO);
}
