package com.hdshop.service.cart;

import com.hdshop.dto.cart.CartItemDTO;
import com.hdshop.dto.cart.CartItemResponse;
import com.hdshop.dto.cart.CartResponse;
import com.hdshop.entity.Cart;

import java.security.Principal;
import java.util.List;

public interface CartService {

    CartItemResponse addToCart(final String username, final CartItemDTO itemDTO);

    CartResponse getCartByUsername(final String username);

    CartResponse getCartById(final Long cartId);

    CartResponse clearItems(final String username);

    CartResponse removeListItems(final String username, final List<Long> itemIds);

    Integer getTotalItems(final Principal principal);

    Cart updateCartTotals(final Cart cart);

    String getTotalPriceForYourCart(final Principal principal);
}
