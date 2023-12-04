package com.hdshop.service.cart;

import com.hdshop.dto.cart.CartItemResponse;

public interface CartItemService {
    CartItemResponse changeQuantity(final Long cartItemId, final int quantity);

    void deleteOneCartItem(final Long cartItemId);
}
