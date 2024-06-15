package com.duck.utils;

import lombok.Getter;

@Getter
public enum EnumOrderStatus {
    ORDERED("ORDERED", "Đã đặt hàng"),
    PROCESSING("PROCESSING", "Đang xử lý"),
    SHIPPING("SHIPPING", "Đang giao"),
    DELIVERED("DELIVERED", "Đã giao"),
    CANCELED("CANCELED", "Đã hủy"),
    WAIT_FOR_PAY("WAIT_FOR_PAY", "Chờ thanh toán"),
    RETURN_REFUND("RETURN_REFUND", "Trả hàng/Hoàn tiền");

    private final String key;

    private final String value;

    EnumOrderStatus(String key, String value) {
            this.key = key;
            this.value = value;
    }
}
