package com.duck.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class VNPayCheckoutReq {
    private Long addressId;
    private BigDecimal total;
    private String note;
}
