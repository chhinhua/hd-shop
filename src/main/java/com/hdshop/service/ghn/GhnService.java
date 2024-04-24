package com.hdshop.service.ghn;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hdshop.dto.ghn.CreateGhnOrderResponse;
import com.hdshop.dto.ghn.GhnOrder;
import com.hdshop.entity.Order;

public interface GhnService {
    CreateGhnOrderResponse createOrder(final GhnOrder order) throws JsonProcessingException;

    GhnOrder buildShippingOrder(final Order order);
}
