package com.hdshop.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserOrderDTO {
    private Long id;
    private String username;
    private String name;
}
