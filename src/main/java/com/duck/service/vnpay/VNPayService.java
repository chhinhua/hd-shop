package com.duck.service.vnpay;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

public interface VNPayService {
    String createOrder(BigDecimal total, String orderInfor, String urlReturn, String orderId);

    String createOrder(BigDecimal total, String orderInfor, String urlReturn);

    int orderReturn(HttpServletRequest request);
}
