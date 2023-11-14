package com.hdshop.dto.order;

import com.hdshop.dto.address.AddressDTO;
import com.hdshop.entity.Address;
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
