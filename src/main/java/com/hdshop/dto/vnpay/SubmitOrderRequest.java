package com.hdshop.dto.vnpay;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubmitOrderRequest {
    Long orderId;
    BigDecimal amount;
    Long addressId;
    String username;
    String note;
}
