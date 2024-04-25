package com.hdshop.service.ghn;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hdshop.dto.ghn.CreateGhnOrderResponse;
import com.hdshop.dto.ghn.GhnOrder;
import com.hdshop.entity.Order;

public interface GhnService {
    String createGhnOrder(final GhnOrder order) throws JsonProcessingException;

    GhnOrder buildShippingOrder(final Order order);
}
