package com.duck.dto.cart;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Hidden
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemProductResponse {
    private Long id;

    private String name;

    private int quantityAvailable;
}
