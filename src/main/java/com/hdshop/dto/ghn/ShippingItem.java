package com.hdshop.dto.ghn;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShippingItem {
    private String name;
    private String code;
    private Long quantity;
    private Long price;
    private Long weight;
}
