package com.hdshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DynamicUpdate
@Table(name = "products")
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "LONGTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String description;

    private String slug;

    private BigDecimal price;

    private BigDecimal promotionalPrice;

    private Integer quantity = 0;

    private Integer quantityAvailable = 0;

    private Integer sold = 0;

    private Float rating;

    private Integer numberOfRatings = 0;

    private Integer favoriteCount = 0;

    private Boolean isActive = true;

    private Boolean isSelling = false;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;

    @ElementCollection
    @Column(name = "imageUrl")
    private List<String> listImages = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.PERSIST)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.PERSIST)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.PERSIST)
    private List<CartItem> cartItems = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<Follow> userFollowProducts = new ArrayList<>();

    @OneToMany(
            mappedBy = "product",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}
    )
    private List<Option> options;

    @OneToMany(
            mappedBy = "product",
            cascade = {CascadeType.MERGE, CascadeType.REMOVE})
    private List<ProductSku> skus;
}
