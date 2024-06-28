package com.duck.entity;

import com.duck.component.SkuGenerator;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "product_skus")
public class ProductSku {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sku_id")
    Long skuId;

    String sku;
    BigDecimal originalPrice;
    BigDecimal price;
    Integer quantity;
    Integer percentDiscount;
    Integer quantityAvailable;
    Integer sold;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    Product product;

    @ManyToMany
    @JoinTable(
            name = "product_sku_option_values",
            joinColumns = @JoinColumn(name = "sku_id"), // Khóa ngoại của product_skus
            inverseJoinColumns = @JoinColumn(name = "value_id") // Khóa ngoại của option_values
    )
    List<OptionValue> optionValues = new ArrayList<>();

    @PrePersist
    @PreUpdate
    public void preAction() {
        this.sku = SkuGenerator.generateSku(product.getProductId(), optionValues);
    }
}