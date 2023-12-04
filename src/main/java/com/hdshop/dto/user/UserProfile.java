package com.hdshop.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfile {
    private Long id;

    @NotBlank(message = "{username-must-not-be-empty}")
    private String username;

    @NotBlank(message = "{email-must-not-be-empty}")
    private String email;

    private String name;

    @Pattern(regexp = "^[0-9]{10,11}$", message = "{invalid-phone-number}")
    private String phoneNumber;

    private String gender;

    private String avatarUrl;
}
