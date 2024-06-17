package com.duck.dto.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderTrackingDTO {
    private Long id;
    private String time;
    private String status;
    private String description;
    private Long orderId;
}
