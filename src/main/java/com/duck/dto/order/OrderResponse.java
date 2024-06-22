package com.duck.dto.order;


import com.duck.dto.address.AddressDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OrderResponse {
    private Long id;
    private String status;
    private String orderCode;
    private String vnpTxnRef;
    private String paymentType;
    private Integer totalItems;
    private BigDecimal subTotal;
    private BigDecimal shippingFee;
    private BigDecimal total;
    private String note;
    private Boolean isPaidBefore;
    private Boolean isDeleted;
    private String createdDate;
    private String lastModifiedDate;
    private Long userId;
    private UserOrderDTO user;
    private AddressDTO address;
    private List<OrderItemResponse> orderItems;
}
