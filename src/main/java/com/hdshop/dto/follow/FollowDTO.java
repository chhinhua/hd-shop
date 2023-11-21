package com.hdshop.dto.wishlist;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WishlistDTO {
    private Long id;
    private Long userId;
    private boolean isDeleted;
    private ProductWishlist product;
}
