package com.hdshop.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;

@Getter
@Setter
public class VNPayCheckoutReq {
    private Long addressId;
    private BigDecimal total;
    private String note;
}
