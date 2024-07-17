package com.duck.service.order;

import com.duck.entity.OrderItem;

public interface OrderItemService {

    OrderItem findById(final Long orderItemId);
}
