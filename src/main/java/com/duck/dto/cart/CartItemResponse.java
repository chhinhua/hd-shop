package com.duck.dto.cart;


import com.duck.dto.product.ProductSkuDTO;
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
