package com.hdshop.dto.order;

import com.hdshop.dto.cart.ItemProductResponse;
import com.hdshop.dto.product.ProductSkuDTO;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.*;

import java.math.BigDecimal;

@Hidden
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO {
    private Long id;

    private Integer quantity;

    private BigDecimal price;

    private BigDecimal subTotal;

    private String imageUrl;

    private Long orderId;

    private Long productId;

    private Long skuId;
}
