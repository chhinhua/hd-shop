package com.hdshop.dto.order;

import com.hdshop.dto.address.AddressDTO;
import com.hdshop.utils.EnumOrderStatus;
import com.hdshop.utils.EnumPaymentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
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

    @NotNull(message = "Total price must be not null and larger or equal to zero")
    private BigDecimal total;

    private String note;

    private Boolean isPaidBefore;

    private String createdBy;

    private String lastModifiedBy;

    private Date createdDate;

    private Date lastModifiedDate;

    private Long userId;

    private Long addressId;

    private List<OrderItemDTO> orderItems;

    private List<Long> cartItemIds;
}
