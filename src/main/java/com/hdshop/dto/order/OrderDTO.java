package com.hdshop.dto.order;

import com.hdshop.dto.AddressDTO;
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

    @NotBlank
    private String status = EnumOrderStatus.PENDING_PROCESSING.getKey();

    @NotBlank
    private String paymentType = EnumPaymentType.COD.getKey();

    @NotNull(message = "Total price must be not null and larger or equal to zero")
    private BigDecimal total;

    private String note;

    private Boolean isPaidBefore = false;

    private String createdBy;

    private String lastModifiedBy;

    private Date createdDate;

    private Date lastModifiedDate;

    @NotNull(message = "User id must be not null")
    private Long userId;

    private AddressDTO address;

    private List<OrderItemDTO> orItems = new ArrayList<>();
}
