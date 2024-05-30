package com.hdshop.dto.address;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressResponse {
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
    Boolean isDefault = false;
    Long userId;
}
