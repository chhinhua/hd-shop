package com.hdshop.entity;

import jakarta.annotation.PostConstruct;
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
@Table(name = "skus")
public class Sku {
    @Id
    private String id; // TODO Tạo id string tự động với (id = idProduct-idVariant-skuCount+1)

    private String size;

    @Column(unique = true)
    private String sku; // TODO Tạo sku string tự động với 1 mẫu
    // TODO Cập nhật ERD (product, variant, sku table, dataType của vài bảng)
    // TODO Cập nhật phần 4.Thiết kế database trong docs

    private Boolean in_stock;

    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    private Variant variant;

    /**
     * Set in_stock is true & sku_price is base price of product if both is null using @PostConstruct annotation
     * @params
     * @return
     */
    @PostConstruct
    public void init() {
        if (in_stock == null) {
            in_stock = true;
        }

        if (price == null && variant != null && variant.getProduct() != null) {
            price = variant.getProduct().getPrice();
        }
    }
}

