package com.duck.utils.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum EOrderTrackingStatus {
    READY_TO_PICK("ready_to_pick", "Mới tạo đơn hàng"),
    PICKING("picking", "Hàng đang được lấy"),
    CANCEL("cancel", "Hủy đơn hàng"),
    MONEY_COLLECT_PICKING("money_collect_picking", "Đang thu tiền người gửi"),
    PICKED("picked", "Hàng đã được lấy"),
    STORING("storing", "Hàng đang nằm ở kho"),
    TRANSPORTING("transporting", "Hàng đang được luân chuyển"),
    SORTING("sorting", "Hàng đang được phân loại"),
    DELIVERING("delivering", "Đơn hàng sẽ sớm được giao, vui lòng chú ý điện thoại"),
    MONEY_COLLECT_DELIVERING("money_collect_delivering", "Nhân viên đang thu tiền người nhận"),
    DELIVERED("delivered", "Giao hàng thành công"),
    DELIVERY_FAIL("delivery_fail", "Giao hàng thất bại"),
    WAITING_TO_RETURN("waiting_to_return", "Đang đợi trả hàng về cho người gửi"),
    RETURN("return", "Trả hàng"),
    RETURN_TRANSPORTING("return_transporting", "Đang luân chuyển hàng trả"),
    RETURN_SORTING("return_sorting", "Hàng trả đang phân loại "),
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
