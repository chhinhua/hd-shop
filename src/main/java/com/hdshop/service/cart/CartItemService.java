package com.hdshop.service.cart;

import com.hdshop.dto.cart.CartItemResponse;
import com.hdshop.entity.CartItem;

import java.util.List;

public interface CartItemService {
    CartItemResponse changeQuantity(final Long cartItemId, final int quantity);

    CartItem findByProductIdAndSkuId(final Long productId, final Long skuId);

    CartItem findById(final Long cartItemId);

    void deleteListItems(final List<Long> ids);

    void delete(final Long cartItemId);
}
