package com.hdshop.dto.product;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OptionDTO {
    private Long optionId;
    private String optionName;
    private List<OptionValueDTO> values;
}
