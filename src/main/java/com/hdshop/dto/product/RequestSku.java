package com.hdshop.dto.product;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RequestSku {
    private Long productId;
    private List<String> valueNames;
}
