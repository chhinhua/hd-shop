package com.hdshop.service.vnpay;

import jakarta.servlet.http.HttpServletRequest;

public interface VNPayService {
    public String createOrder(int total, String orderInfor, String urlReturn);

    public int orderReturn(HttpServletRequest request);
}
