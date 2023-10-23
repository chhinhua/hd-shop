package com.hdshop.dto.order;

import com.hdshop.dto.AddressDTO;
import com.hdshop.utils.EnumOrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;

    @Enumerated(EnumType.STRING)
    private EnumOrderStatus status = EnumOrderStatus.PENDING_PROCESSING;

    @NotNull(message = "Total price must be not null and larger or equal to zero")
    private BigDecimal total;

    private String note;

    private Boolean isPaidBefore = false;

    // TODO thiết kết kiểu thanh toán (enum or string)
    private String paymentType;

    @NotNull(message = "User id must be not null")
    private Long userId;

    private AddressDTO address;

    private List<OrderItemDTO> orItems = new ArrayList<>();
}
