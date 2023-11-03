package com.hdshop.dto.cart;

import com.hdshop.dto.product.ProductSkuDTO;
import com.hdshop.entity.Product;
import com.hdshop.entity.ProductSku;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Hidden
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
        private Long id;

        private Integer quantity;

        private BigDecimal price;

        private BigDecimal subTotal;

        private Long cartId;

        private Long productId;

        private Long skuId;
}
