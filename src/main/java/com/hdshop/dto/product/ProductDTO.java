package com.hdshop.dto.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hdshop.dto.category.CategoryDTO;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Hidden
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDTO {
    Long id;

    String name;

    String description;

    BigDecimal price;

    Integer quantity;

    Integer productClicks;

    Integer productViews;

    Integer productCartAdds;

    int quantityAvailable;

    Long categoryId;

    String categoryName;

    String slug;

    BigDecimal promotionalPrice;

    int sold;

    float rating;

    int numberOfRatings;

    int favoriteCount;

    boolean liked;

    Boolean isActive;

    Boolean isSelling;

    String createdDate;

    String lastModifiedDate;

    CategoryDTO category;

    String createdBy;

    String lastModifiedBy;

    List<String> listImages = new ArrayList<>();

    List<OptionDTO> options = new ArrayList<>();

    List<ProductSkuDTO> skus = new ArrayList<>();
}
