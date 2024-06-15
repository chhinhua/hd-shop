package com.duck.service.ghn;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonObject;
import com.duck.dto.ghn.GhnOrder;
import com.duck.entity.Address;
import com.duck.entity.Order;
import com.duck.utils.EnumOrderStatus;

import java.math.BigDecimal;

public interface GhnService {
    String createGhnOrder(final GhnOrder order) throws JsonProcessingException;

    String getOrderStatus(final String orderCode) throws JsonProcessingException;

    GhnOrder buildGhnOrder(final Order order);

    JsonObject getOrderDetail(final String orderCode) throws JsonProcessingException;

    void cancelGhnOrder(final String orderCode) throws JsonProcessingException;

    EnumOrderStatus getEnumStatus(final String ghnOrderStatus);

    BigDecimal calculateFee(final Address address);

    Long calculateHeight(final int itemCount);
}
