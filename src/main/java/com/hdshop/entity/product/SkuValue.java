package com.hdshop.entity.product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sku_values")
public class SkuValue {
    @EmbeddedId
    private SkuValueId skuValueId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "product_id", referencedColumnName = "product_id", insertable = false, updatable = false),
            @JoinColumn(name = "option_id", referencedColumnName = "option_id", insertable = false, updatable = false)
    })
    @MapsId("skuValueId")
    private Option option;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "product_id", referencedColumnName = "product_id", insertable = false, updatable = false),
            @JoinColumn(name = "sku_id", referencedColumnName = "sku_id", insertable = false, updatable = false)
    })
    @MapsId("skuValueId")
    private ProductSku productSku;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "product_id", referencedColumnName = "product_id", insertable = false, updatable = false),
            @JoinColumn(name = "option_id", referencedColumnName = "option_id", insertable = false, updatable = false),
            @JoinColumn(name = "value_id", referencedColumnName = "value_id", insertable = false, updatable = false)
    })
    @MapsId("skuValueId")
    private OptionValue optionValue;
}
