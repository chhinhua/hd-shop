package com.hdshop.service.ghn;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hdshop.dto.ghn.ShippingOrder;
import com.hdshop.entity.Order;

public interface GhnService {
    ShippingOrder createOrder(final ShippingOrder order) throws JsonProcessingException;

    ShippingOrder buildShippingOrder(final Order order);
}
