package com.hdshop.entity.product;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Embeddable
public class ProductSkuId implements Serializable {
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "sku_id")
    private Long skuId;
}
