package com.hdshop.utils;

import lombok.Getter;

@Getter
public enum EnumOrderStatus {
    PENDING_PROCESSING("PENDING_PROCESSING", "Đang chờ xử lý"),
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
