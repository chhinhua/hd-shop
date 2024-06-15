package com.duck.dto.product;

import com.duck.dto.category.CategoryDTO;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.ArrayList;
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
