package com.hdshop.service.wishlist;

import com.hdshop.dto.wishlist.WishlistDTO;

import java.security.Principal;

public interface WishlistService {
    WishlistDTO like(final Long productId, final Principal principal);
}
