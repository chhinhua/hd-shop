package com.hdshop.utils;

import lombok.Getter;

@Getter
public enum EnumOrderStatus {
    ORDERED("ORDERED", "Đã đặt hàng"),
    PROCESSING("PROCESSING", "Đang xử lý"),
    SHIPPED("SHIPPED", "Đã gửi"),
    DELIVERED("DELIVERED", "Đã giao"),
    CANCELED("CANCELED", "Đã hủy");

    private final String key;

    private final String value;

    EnumOrderStatus(String key, String value) {
            this.key = key;
            this.value = value;
    }
}
