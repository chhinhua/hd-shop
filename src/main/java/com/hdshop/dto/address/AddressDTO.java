package com.hdshop.dto.address;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Hidden
public class AddressDTO {
    private Long id;

    @NotBlank(message = "Full Name must not be empty")
    private String fullName;

    @Pattern(regexp = "^[0-9]{10,11}$", message = "Số điện thoại không hợp lệ")
    private String phoneNumber;

    @NotBlank(message = "City must not be empty")
    private String city;

    @NotBlank(message = "District must not be empty")
    private String district;

    @NotBlank(message = "Ward must not be empty")
    private String ward;

    private String orderDetails;

    private Boolean isDefault;

    private Long userId;
}
