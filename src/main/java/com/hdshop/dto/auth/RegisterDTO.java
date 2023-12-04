package com.hdshop.dto.auth;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Hidden
public class RegisterDTO {
    private String username;

    private String email;

    private String password;
}
