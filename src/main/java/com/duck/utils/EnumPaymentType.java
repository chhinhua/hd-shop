package com.duck.utils;

import lombok.Getter;

@Getter
public enum EnumPaymentType {
    VN_PAY("VNPay", "Thanh toán trực tuyến với VNPay"),
    COD("Cash_On_Delivery", "Thanh toán khi nhận hàng");

    private final String key;
    private final String value;

    EnumPaymentType(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
