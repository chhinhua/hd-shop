package com.duck.dto.product;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Hidden
public class OptionDTO {
    private Long optionId;
    private String optionName;
    private List<OptionValueDTO> values;
}
