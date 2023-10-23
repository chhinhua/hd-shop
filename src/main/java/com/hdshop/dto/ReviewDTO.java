package com.hdshop.dto;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

@Hidden
public class ReviewDTO {
        private Long id;

        @NotBlank(message = "Content is required")
        private String content;

        @Range(min = 0, max = 5, message = "Giá trị stars phải nằm trong khoảng từ 0 đến 5")
        private Integer stars;

        @NotNull(message = "User id must be not null")
        private Long userId;

        @NotNull(message = "Product id must be not null")
        private Long productId;

        @NotNull(message = "Order id must be not null")
        private Long orderId;
    }


