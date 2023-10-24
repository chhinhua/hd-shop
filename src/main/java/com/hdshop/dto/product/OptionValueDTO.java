package com.hdshop.dto.product;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Hidden
public class OptionValueDTO {
    private Long valueId;
    private String valueName;
    private String imageUrl;
}
