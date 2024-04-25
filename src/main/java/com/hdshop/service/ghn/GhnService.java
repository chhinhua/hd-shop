package com.hdshop.service.ghn;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hdshop.dto.ghn.GhnOrder;
import com.hdshop.entity.Order;
import com.hdshop.utils.EnumOrderStatus;

public interface GhnService {
    String createGhnOrder(final GhnOrder order) throws JsonProcessingException;

    GhnOrder buildShippingOrder(final Order order);

    Object getOrderDetail(final String orderCode);

    EnumOrderStatus getEnumStatus(final String ghnOrderStatus);

    String getOrderStatus(final String orderCode);

    void cancelGhnOrder(final String orderCode) throws JsonProcessingException;
}
