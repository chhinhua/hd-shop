package com.duck.dto.follow;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FollowDTO {
    private Long id;
    private Long userId;
    private Boolean isDeleted;
    private ProductFollow product;
}
