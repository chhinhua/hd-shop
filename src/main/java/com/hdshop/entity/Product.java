package com.hdshop.entity;

import com.hdshop.listener.EntityListener;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@Entity
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(EntityListener.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "products", indexes = {
        @Index(name = "idx_product_name", columnList = "name"),
        @Index(name = "idx_product_price", columnList = "price"),
        @Index(name = "idx_product_number_of_ratings", columnList = "numberOfRatings")
})
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long productId;

    @Column(nullable = false)
    String name;

    @Column(columnDefinition = "LONGTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String description;

    String slug;
    BigDecimal originalPrice;
    BigDecimal percentDiscount;
    BigDecimal price;
    BigDecimal promotionalPrice;
    Integer quantity;
    Integer quantityAvailable;
    Integer sold;
    Integer numberOfRatings;
    Integer favoriteCount;

    Integer productClicks;
    Integer productViews;
    Integer productCartAdds;
    Float rating;
    Boolean isActive;
    Boolean isSelling;

    @CreatedBy
    String createdBy;

    @LastModifiedBy
    String lastModifiedBy;

    @ElementCollection
    @Column(name = "imageUrl")
    List<String> listImages = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE)
    List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    List<OrderItem> orderItems = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    List<CartItem> cartItems = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    List<Follow> userFollowProducts = new ArrayList<>();

    @OneToMany(
            mappedBy = "product",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}
    )
    List<Option> options;

    @OneToMany(
            mappedBy = "product",
            cascade = {CascadeType.REMOVE})
    List<ProductSku> skus;
}

