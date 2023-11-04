package com.hdshop.dto.cart;


import com.hdshop.dto.product.ProductSkuDTO;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
    private Long id;

    private Integer quantity;

    private BigDecimal price;

    private BigDecimal subTotal;

    private String imageUrl;

    private Long cartId;

    private ItemProductResponse product;

    private ProductSkuDTO sku;
}
