package com.duck.dto.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Hidden
public class OptionValueDTO {
    @JsonIgnore
    private Long valueId;
    private String valueName;
    private String imageUrl;
}
