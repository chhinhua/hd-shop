package com.hdshop.service.order;

import com.hdshop.entity.OrderItem;

public interface OrderItemService {
    OrderItem findById(final Long orderItemId);
}
