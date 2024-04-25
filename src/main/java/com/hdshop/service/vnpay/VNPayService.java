package com.hdshop.service.vnpay;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.security.Principal;

public interface VNPayService {
    String createOrder(BigDecimal total, String orderInfor, String urlReturn);

    int orderReturn(HttpServletRequest request);
}
