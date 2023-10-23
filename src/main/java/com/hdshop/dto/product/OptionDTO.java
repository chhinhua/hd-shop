package com.hdshop.dto.product;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Hidden
public class OptionDTO {
    private Long optionId;
    private String optionName;
    private List<OptionValueDTO> values;
}
