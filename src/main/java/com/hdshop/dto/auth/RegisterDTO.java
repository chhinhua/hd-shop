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
    @NotBlank(message = "Username is required")
    @Pattern(regexp = "^[a-zA-Z0-9]{4,}$", message = "Invalid username")
    private String username;

    @NotEmpty(message = "Email is required")
    @Email(message = "Email is invalid")
    private String email;

    @Size(min = 8, message = "Password must be minimum 8 characters")
    private String password;
}
