package com.hdshop.service.cart;

import com.hdshop.dto.cart.CartItemResponse;
import com.hdshop.entity.CartItem;

public interface CartItemService {
    CartItemResponse changeQuantity(final Long cartItemId, final int quantity);

    void delete(final Long cartItemId);
    CartItem findById(final Long cartItemId);
}
