package com.hdshop.dto.ghn;

import lombok.Builder;
import lombok.Data;

import java.util.List;

// Documentation 🔗https://api.ghn.vn/home/docs/detail?id=122

@Data
@Builder
public class ShippingOrder {
    private Long payment_type_id; // Required
    private String note; // Optional
    private String required_note; // Required (Include: CHOTHUHANG, CHOXEMHANGKHONGTHU, KHONGCHOXEMHANG)
    private String to_name; // Required
    private String to_phone; // Required
    private String to_address; // Required
    private String to_ward_name; // Required
    private String to_district_name; // Required
    private String to_province_name; // Required
    private Long weight; // Required (max 30000gram)
    private Long length; // Required (max 150cm)
    private Long width; // Required (max 150cm)
    private Long height; // Required (max 150cm)
    private Long service_type_id; // Required
    private String from_name;
    private String from_phone;
    private String from_address;
    private String from_ward_name;
    private String from_district_name;
    private String from_province_name;
    private String return_phone;
    private String return_address;
    private Long return_district_id;
    private String return_ward_code;
    private String client_order_code;
    private Long cod_amount;
    private String content;
    private String coupon;
    private Long pick_station_id;
    private Long deliver_station_id;
    private Long insurance_value;
    private Long service_id;
    private List<ShippingItem> items;
    private Long cod_failed_amount;
    private Long pickup_time;
    private List<Integer> pick_shift;
}
