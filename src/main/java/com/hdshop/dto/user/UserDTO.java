package com.hdshop.dto.user;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Hidden
public class UserDTO {
    private Long id;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    private String firstName;

    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email address")
    private String email;

    @Pattern(regexp = "^[0-9]{9}$|^[0-9]{12}$", message = "Số CMND/CCCD không hợp lệ")
    private String id_card;

    @Pattern(regexp = "^[0-9]{10,11}$", message = "Số điện thoại không hợp lệ")
    private String phoneNumber;

    private String gender;

    private LocalDate dateOfBirth;

    private String avatarUrl;

    private Boolean isEnable = false;

    private String createdBy;

    private String lastModifiedBy;

    private Date createdDate;

    private Date lastModifiedDate;
}
