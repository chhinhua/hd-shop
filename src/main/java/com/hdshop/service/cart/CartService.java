package com.hdshop.service.cart;

import com.hdshop.dto.cart.CartItemDTO;
import com.hdshop.dto.cart.CartItemResponse;
import com.hdshop.dto.cart.CartResponse;
import com.hdshop.entity.Cart;

public interface CartService {

    CartItemResponse addToCart(final Long cartId, final CartItemDTO itemDTO);

    Cart getCartByUsername(final String username);

    CartItemResponse changeQuantity(final Long cartItemId, final int quantity);

    void deleteOneCartItem(final Long cartItemId);

    CartResponse getCartById(final Long cartId);
}
