package com.hdshop.dto.address;

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
public class AddressResponse {
    private Long id;

    private String fullName;

    private String phoneNumber;

    private String city;

    private String district;

    private String ward;

    private String orderDetails;

    private Boolean isDefault = false;

    private Long userId;
}
