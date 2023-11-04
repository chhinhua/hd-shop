package com.hdshop.service.cart;

import com.hdshop.dto.cart.CartItemDTO;
import com.hdshop.dto.cart.CartItemResponse;
import com.hdshop.dto.cart.CartResponse;
import com.hdshop.entity.Cart;

import java.util.List;

public interface CartService {

    CartItemResponse addToCart(final Long cartId, final CartItemDTO itemDTO);

    Cart getCartByUsername(final String username);

    CartResponse getCartById(final Long cartId);

    CartResponse clearItems(final String username);

    CartResponse removeListItems(final String username, final List<Long> itemIds);
}
