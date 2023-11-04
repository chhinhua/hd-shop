package com.hdshop.dto.cart;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private Long id;
    private Boolean isDeleted;
    private List<CartItemResponse> cartItems;
    private long totalItems;
    private BigDecimal totalPrice;
}
