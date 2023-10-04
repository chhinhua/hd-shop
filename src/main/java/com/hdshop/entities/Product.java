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
    private List<String> listImages = new ArrayList<String>();


    /*@OneToMany
    @JoinColumn(name = "category_id")
    private Set<Category> categories;*/

    @OneToMany(mappedBy = "product")
    private Set<Review> reviews;
}
