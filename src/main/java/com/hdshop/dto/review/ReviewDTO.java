package com.hdshop.dto.review;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.util.Date;

@Hidden
@Getter
@Setter
public class ReviewDTO {
    private Long id;

    private String content;

    private Integer stars;

    private Date createdDate;

    private UserDTO user;

    private Long productId;

    private Long orderId;
}


