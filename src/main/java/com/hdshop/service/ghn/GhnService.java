package com.hdshop.service.ghn;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonObject;
import com.hdshop.dto.ghn.GhnOrder;
import com.hdshop.entity.Order;
import com.hdshop.utils.EnumOrderStatus;

public interface GhnService {
    String createGhnOrder(final GhnOrder order) throws JsonProcessingException;

    GhnOrder buildGhnOrder(final Order order);

    JsonObject getOrderDetail(final String orderCode) throws JsonProcessingException;

    EnumOrderStatus getEnumStatus(final String ghnOrderStatus);

    String getOrderStatus(final String orderCode) throws JsonProcessingException;

    void cancelGhnOrder(final String orderCode) throws JsonProcessingException;
}
