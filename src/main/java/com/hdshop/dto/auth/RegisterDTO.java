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
    @Pattern(
            regexp = "^[a-zA-Z0-9]{4,}$",
            message = "{invalid-username} ({cannot-be-less-than-4-characters})"
    )
    private String username;

    @Email(message = "{invalid-email-address}")
    private String email;

    @Size(min = 8, message = "{password-length-cannot-be-less-than-8-characters}")
    private String password;
}
