package com.hdshop.service.order;

import com.hdshop.dto.order.OrderItemDTO;
import com.hdshop.entity.OrderItem;

public interface OrderItemService {
    OrderItem findById(final Long orderItemId);

    OrderItem mapToOrderItem(final OrderItemDTO itemDTO);
}
