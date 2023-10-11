package com.hdshop.dtos.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class CreateProductDTO {
    @NotBlank(message = "Product name must not be empty")
    private String name;

    @NotBlank(message = "Product description must not be empty")
    @Size(max = 1000, message = "Product description must not exceed 1000 characters")
    private String description;

    private BigDecimal price;

    @Min(value = 1, message = "Quantity must be larger than 1")
    @NotNull(message = "Quantity is required")
    private int quantity;

    private List<String> listImages = new ArrayList<String>();

    @NotNull(message = "CategoryId is required")
    private Long categoryId;
}
