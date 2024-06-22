package com.duck.dto.review;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.Getter;
import lombok.Setter;

@Hidden
@Getter
@Setter
public class ReviewDTO {
    private Long id;

    private String content;

    private Integer stars;

    private String createdDate;

    private UserDTO user;

    private Long productId;

    private Long orderId;

    private Long itemId;

    private String sku;
}


