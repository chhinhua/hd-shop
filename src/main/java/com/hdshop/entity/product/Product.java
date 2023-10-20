    package com.hdshop.entity.product;

    import com.hdshop.entity.*;
    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;
    import org.springframework.data.annotation.CreatedBy;
    import org.springframework.data.annotation.LastModifiedBy;

    import java.math.BigDecimal;
    import java.util.*;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Entity
    @Table(name = "products")
    public class Product extends BaseEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, unique = true)
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

        private Boolean isSelling = true;

        @CreatedBy
        private String createdBy;

        @LastModifiedBy
        private String lastModifiedBy;

        @ElementCollection
        @Column(name = "imageUrl")
        private List<String> listImages = new ArrayList<String>();

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
        private List<UserFollowProduct> userFollowProducts = new ArrayList<>();

        @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
        private List<Option> options = new ArrayList<>();

        @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
        private List<ProductSku> skus = new ArrayList<>();
    }
