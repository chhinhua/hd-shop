package com.hdshop.utils;

import lombok.Getter;

@Getter
public enum EnumOrderStatus {
    ORDERED("ORDERED", "Đã đặt hàng"),
    PROCESSING("PROCESSING", "Đang xử lý"),
    SHIPPED("SHIPPED", "Đã gửi"),
    DELIVERED("DELIVERED", "Đã giao"),
    CANCELED("CANCELED", "Đã hủy"),
    WAIT_FOR_PAY("WAIT_FOR_PAY", "Chờ thanh toán");

    private final String key;

    private final String value;

    EnumOrderStatus(String key, String value) {
            this.key = key;
            this.value = value;
    }
}
