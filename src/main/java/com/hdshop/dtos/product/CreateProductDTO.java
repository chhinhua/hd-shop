package com.hdshop.dtos.product;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CreateProductDTO {
    private Long id;

    @NotEmpty(message = "Product name must not be empty")
    private String name;

    @NotEmpty(message = "Product description must not be empty")
    private String description;

    // TODO thêm brand lúc thêm sản phẩm nếu brand cần chưa có
    private String brand;

    private BigDecimal price;

    private Integer quantity;

    private Integer quantityAvailable;

    private List<String> listImages = new ArrayList<String>();

    @NotNull(message = "Category-Id is required")
    private Long categoryId;
}
