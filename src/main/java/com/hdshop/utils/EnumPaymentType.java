package com.hdshop.utils;

import lombok.Getter;

@Getter
public enum EnumPaymentType {
    VN_PAY("VNPay", "Thanh toán bằng VNPay"),
    COD("Cash on Delivery", "Thanh toán khi nhận hàng");

    private final String key;
    private final String value;

    EnumPaymentType(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
