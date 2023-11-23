package com.hdshop.dto.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hdshop.dto.category.CategoryDTO;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Hidden
public class ProductDTO {
    private Long id;

    private String name;

    private String description;

    private BigDecimal price;

    private Integer quantity;

    private int quantityAvailable;

    private Long categoryId;

    private String categoryName;

    private String slug;

    private BigDecimal promotionalPrice;

    private int sold;

    private float rating;

    private int numberOfRatings;

    private int favoriteCount;

    private boolean liked;

    private Boolean isActive;

    private Boolean isSelling;

    private Date createdDate;

    private CategoryDTO category;

    private Date lastModifiedDate;

    private String createdBy;

    private String lastModifiedBy;

    private List<String> listImages = new ArrayList<>();

    private List<OptionDTO> options = new ArrayList<>();

    private List<ProductSkuDTO> skus = new ArrayList<>();
}
