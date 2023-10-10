    package com.hdshop.entities;

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

        @Column(nullable = false)
        private String name;

        private String description;

        private String brand;

        private String slug;

        private BigDecimal price;

        private BigDecimal promotionalPrice;

        private Integer quantity;

        private Integer quantityAvailable;

        private Integer sold;

        private Float rating;

        private Integer numberOfRatings;

        private Integer favoriteCount;

        private boolean isActive = true;

        private boolean isSelling;

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

        @OneToMany(mappedBy = "product")
        private Set<Review> reviews;

        @OneToMany(mappedBy = "product")
        private List<OrderItem> orderItems = new ArrayList<>();

        @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
        private List<CartItem> cartItems = new ArrayList<>();

        @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
        private List<UserFollowProduct> userFollowProducts = new ArrayList<>();
    }
