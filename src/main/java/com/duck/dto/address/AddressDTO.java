package com.duck.dto.address;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Hidden
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressDTO {
    Long id;
    String fullName;
    String phoneNumber;
    String province;
    Integer provinceId;
    String district;
    Integer districtId;
    String ward;
    String wardCode;
    String orderDetails;
    Boolean isDefault;
    Long userId;
}
