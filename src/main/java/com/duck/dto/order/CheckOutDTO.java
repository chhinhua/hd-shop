package com.duck.dto.order;

import com.duck.dto.address.AddressDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CheckOutDTO {
    private List<AddressDTO> addresses;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal total;
}
