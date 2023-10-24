package com.hdshop.dto.product;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Hidden
public class ProductSkuDTO {
    private Long skuId;
    private List<OptionValueDTO> optionValues;
    private Double price;
}
