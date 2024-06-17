package com.duck.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum EOrderTrackingStatus {
    READY_TO_PICK("ready_to_pick", "Mới tạo đơn hàng"),
    PICKING("picking", "Nhân viên đang lấy hàng"),
    CANCEL("cancel", "Hủy đơn hàng"),
    MONEY_COLLECT_PICKING("money_collect_picking", "Đang thu tiền người gửi"),
    PICKED("picked", "Nhân viên đã lấy hàng"),
    STORING("storing", "Hàng đang nằm ở kho"),
    TRANSPORTING("transporting", "Đang luân chuyển hàng"),
    SORTING("sorting", "Đang phân loại hàng hóa"),
    DELIVERING("delivering", "Nhân viên đang giao cho người nhận"),
    MONEY_COLLECT_DELIVERING("money_collect_delivering", "Nhân viên đang thu tiền người nhận"),
    DELIVERED("delivered", "Nhân viên đã giao hàng thành công"),
    DELIVERY_FAIL("delivery_fail", "Nhân viên giao hàng thất bại"),
    WAITING_TO_RETURN("waiting_to_return", "Đang đợi trả hàng về cho người gửi"),
    RETURN("return", "Trả hàng"),
    RETURN_TRANSPORTING("return_transporting", "Đang luân chuyển hàng trả"),
    RETURN_SORTING("return_sorting", "Đang phân loại hàng trả"),
    RETURNING("returning", "Nhân viên đang đi trả hàng"),
    RETURN_FAIL("return_fail", "Nhân viên trả hàng thất bại"),
    RETURNED("returned", "Nhân viên trả hàng thành công"),
    EXCEPTION("exception", "Đơn hàng ngoại lệ không nằm trong quy trình"),
    DAMAGE("damage", "Hàng bị hư hỏng"),
    LOST("lost", "Hàng bị mất");

    private final String status;
    private final String description;

    EOrderTrackingStatus(String status, String description) {
        this.status = status;
        this.description = description;
    }

    @JsonCreator
    public static EOrderTrackingStatus fromStatus(String status) {
        for (EOrderTrackingStatus tracking : EOrderTrackingStatus.values()) {
            if (tracking.status.equals(status)) {
                return tracking;
            }
        }
        throw new IllegalArgumentException("Unknown order status code: " + status);
    }
}
