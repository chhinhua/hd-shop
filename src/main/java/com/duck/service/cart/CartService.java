package com.duck.service.cart;

import com.duck.dto.cart.CartItemDTO;
import com.duck.dto.cart.CartItemResponse;
import com.duck.dto.cart.CartResponse;
import com.duck.entity.Cart;

import java.security.Principal;
import java.util.List;

public interface CartService {

    CartItemResponse addToCart(final String username, final CartItemDTO itemDTO);

    CartResponse getCartByUsername(final String username);

    Cart findByUsername(final String username);

    CartResponse getCartById(final Long cartId);

    CartResponse clearItems(final String username);

    CartResponse removeListItems(final String username, final List<Long> itemIds);

    Integer getTotalItems(final Principal principal);

    Cart updateCartTotals(final Cart cart);

    String getTotalPriceForYourCart(final Principal principal);
}
