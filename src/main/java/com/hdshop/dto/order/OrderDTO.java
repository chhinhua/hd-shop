package com.hdshop.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class OrderDTO {
    private Long id;

    private String status;

    @NotBlank
    private String paymentType;

    private BigDecimal subTotal;
    private BigDecimal shippingFee;

    @NotNull(message = "Total price must be not null and larger or equal to zero")
    private BigDecimal total;

    private String note;

    private Boolean isPaidBefore;

    private String createdBy;

    private String lastModifiedBy;

    private Long userId;

    private Long addressId;

    private List<OrderItemDTO> orderItems;

    private List<Long> cartItemIds;
}
