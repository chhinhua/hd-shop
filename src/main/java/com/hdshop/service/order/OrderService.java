package com.hdshop.service.order;

import com.hdshop.dto.OrderDTO;

public interface OrderService {
    OrderDTO createOrder(final OrderDTO order);
}
