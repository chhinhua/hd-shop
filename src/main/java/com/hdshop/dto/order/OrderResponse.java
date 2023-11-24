package com.hdshop.dto.order;


import com.hdshop.dto.address.AddressDTO;
import com.hdshop.dto.address.AddressResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;

    private String status;

    private String paymentType;

    private Integer totalItems;

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