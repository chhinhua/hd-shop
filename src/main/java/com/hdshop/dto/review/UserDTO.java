package com.hdshop.dto.review;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Hidden
@Getter
@Setter
@NoArgsConstructor
public class UserDTO {
    private String username;
    private String avatarUrl;
}
