package com.hdshop.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FollowProductDTO {
    private Long id;
    private Long productId;
    private Long userId;
    private boolean isDeleted;
}
