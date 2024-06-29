package com.duck.utils.enums;

import lombok.Getter;

@Getter
public enum EOrderStatus {
    ORDERED("ORDERED", "Đã đặt hàng"),
    PROCESSING("PROCESSING", "Đang xử lý"),
    SHIPPING("SHIPPING", "Đang giao"),
    DELIVERED("DELIVERED", "Đã giao"),
    CANCELED("CANCELED", "Đã hủy"),
    WAIT_FOR_PAY("WAIT_FOR_PAY", "Chờ thanh toán"),
    RETURN_REFUND("RETURN_REFUND", "Trả hàng/Hoàn tiền");

    private final String key;
    private final String value;

    EOrderStatus(String key, String value) {
            this.key = key;
            this.value = value;
    }

    public static EOrderStatus fromOrderTrackingStatus(String status) {
        switch (status) {
            case "ready_to_pick" -> {
                return EOrderStatus.ORDERED; // đã đặt/mới tạo đơn
            }
            case "picking", "money_collect_picking", "picked", "storing", "sorting", "transporting" -> {
                return EOrderStatus.PROCESSING; // đang xử lý
            }
            case "delivering", "money_collect_delivering" -> {
                return EOrderStatus.SHIPPING; // đang giao
            }
            case "delivered" -> {
                return EOrderStatus.DELIVERED; // đã giao/hoàn thành
            }
            case "waiting_to_return", "return", "return_transporting", "return_sorting", "returning", "return_fail", "returned" -> {
                return EOrderStatus.RETURN_REFUND; // trả hàng/hoàn tiền
            }
            case "cancel" -> {
                return EOrderStatus.CANCELED; // hủy
            }
            default -> {
                return null; //exception", "damage", "lost
            }
        }
    }
}
