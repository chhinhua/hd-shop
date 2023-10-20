package com.hdshop.dto.product;

import com.hdshop.entity.product.SkuValue;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProductSkuDTO {
    private Long skuId;
    private List<OptionValueDTO> optionValues;
    private List<SkuValueDTO> skuValues;
    private Double price;
}
