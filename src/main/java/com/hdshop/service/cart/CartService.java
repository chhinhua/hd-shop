package com.hdshop.service.cart;

import com.hdshop.dto.cart.CartItemDTO;
import com.hdshop.entity.Cart;

public interface CartService {
    CartItemDTO addToCart(final Long cartId, final CartItemDTO itemDTO);

    Cart getCartByUsername(final String username);
}
