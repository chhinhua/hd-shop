package com.duck.service.cart;

import com.duck.dto.cart.CartItemResponse;
import com.duck.entity.CartItem;

import java.util.List;

public interface CartItemService {
    CartItemResponse changeQuantity(final Long cartItemId, final int quantity);

    CartItem findByProductIdAndSkuId(final Long productId, final Long skuId);

    CartItem findById(final Long cartItemId);

    void deleteListItems(final List<Long> ids);

    void delete(final Long cartItemId);
}
