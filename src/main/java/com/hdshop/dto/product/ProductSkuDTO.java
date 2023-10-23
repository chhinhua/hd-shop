package com.hdshop.dto.product;

import com.hdshop.entity.product.SkuValue;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Hidden
public class ProductSkuDTO {
    private Long skuId;
    private List<OptionValueDTO> optionValues;
    private List<SkuValueDTO> skuValues;
    private Double price;
}
