package com.hdshop.dto.user;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Hidden
public class UserDTO {
    private Long id;

    @NotBlank(message = "{username-must-not-be-empty}")
    private String username;

    @NotBlank(message = "{password-must-not-be-empty}")
    private String password;

    @NotBlank(message = "{email-must-not-be-empty}")
    @Email(message = "{invalid-email-address}")
    private String email;

    private String name;

    @Pattern(regexp = "^[0-9]{10,11}$", message = "{invalid-phone-number}")
    private String phoneNumber;

    private String gender;

    private String avatarUrl;

    private Boolean isEnabled;

    private boolean isLocked;

    private String lastModifiedBy;

    private String createdDate;

    private String lastModifiedDate;
}
