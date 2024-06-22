package com.duck.dto.follow;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ProductFollow {
    private Long productId;
    private String name;
    private String imageUrl;
    private BigDecimal price;
}
