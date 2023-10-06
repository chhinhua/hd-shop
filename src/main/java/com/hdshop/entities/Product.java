package com.hdshop.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private String slug;

    private BigDecimal price;

    private BigDecimal promotionalPrice;

    private Integer quantity;

    private Integer sold;

    private Boolean isActive;

    private Boolean isSelling;

    private Float rating;

    private Date createAt;

    private Date updateAt;

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
}
