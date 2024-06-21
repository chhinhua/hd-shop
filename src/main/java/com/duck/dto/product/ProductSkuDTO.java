package com.duck.dto.product;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Hidden
public class ProductSkuDTO {
    private Long skuId;
    private String sku;
    BigDecimal originalPrice;
    BigDecimal price;
    Integer percentDiscount;
    Integer quantity;
    Integer quantityAvailable;
    Integer sold;
    Long productId;
    private List<OptionValueDTO> optionValues;
}
