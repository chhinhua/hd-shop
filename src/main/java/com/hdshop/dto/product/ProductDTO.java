package com.hdshop.dto.product;

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
    @NotBlank(message = "Product name must not be empty")
    private String name;

    @NotBlank(message = "Product description must not be empty")
    @Size(max = 1000, message = "Product description must not exceed 1000 characters")
    private String description;

    private BigDecimal price;

    @Min(value = 1, message = "Quantity must be larger than 1")
    @NotNull(message = "Quantity is required")
    private Integer quantity;

    @Min(value = 1, message = "Quantity must be larger than 1")
    @NotNull(message = "Quantity is required")
    private int quantityAvailable;

    @NotNull(message = "CategoryId is required")
    private Long categoryId;

    private String slug;

    private BigDecimal promotionalPrice;

    private int sold = 0;

    private float rating = 0;

    private int numberOfRatings = 0;

    private int favoriteCount = 0;

    private Boolean isActive = true;

    private Boolean isSelling = true;

    private Date createdDate;

    private Date lastModifiedDate;

    private String createdBy;

    private String lastModifiedBy;

    private List<String> listImages = new ArrayList<String>();

    private List<OptionDTO> options = new ArrayList<>();

    private List<ProductSkuDTO> skus = new ArrayList<>();
}
