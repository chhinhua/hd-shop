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
    BigDecimal originalPrice;
    Integer percentDiscount;
    BigDecimal promotionalPrice;
    Integer quantity;
    Integer productClicks;
    Integer productViews;
    Integer productCartAdds;
    Integer quantityAvailable;
    Integer sold;
    Integer numberOfRatings;
    Integer favoriteCount;
    Float rating;
    Boolean liked;
    Boolean isActive;
    Boolean isSelling;
    String status;
    Long categoryId;
    String slug;
    String categoryName;
    String createdDate;
    String lastModifiedDate;
    String createdBy;
    String lastModifiedBy;
    CategoryDTO category;
    List<String> listImages = new ArrayList<>();
    List<OptionDTO> options = new ArrayList<>();
    List<ProductSkuDTO> skus = new ArrayList<>();
}
