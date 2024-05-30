package com.hdshop.dto.order;

import com.hdshop.dto.cart.ItemProductResponse;
import com.hdshop.dto.product.ProductSkuDTO;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Hidden
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemResponse {
    Long id;

    Integer quantity;

    BigDecimal price;

    BigDecimal subTotal;

    String imageUrl;

    Long orderId;

    ItemProductResponse product;

    ProductSkuDTO sku;

    boolean hasReview;
}
