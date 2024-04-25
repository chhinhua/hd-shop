package com.hdshop.service.cart.impl;

import com.hdshop.dto.cart.CartItemResponse;
import com.hdshop.entity.Cart;
import com.hdshop.entity.CartItem;
import com.hdshop.exception.ResourceNotFoundException;
import com.hdshop.repository.*;
import com.hdshop.service.cart.CartItemService;
import com.hdshop.service.cart.CartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartItemServiceImpl implements CartItemService {
    CartItemRepository cartItemRepository;
    CartService cartService;
    MessageSource messageSource;
    ModelMapper modelMapper;

    /**
     * Overrides the method to change the quantity of a CartItem.
     *
     * @param cartItemId The ID of the CartItem to be updated.
     * @param quantity The new quantity for the CartItem.
     * @return A CartItemDTO representing the updated CartItem.
     * @throws ResourceNotFoundException if a CartItem with the given ID is not found.
     */
    @Override
    @Transactional
    public CartItemResponse changeQuantity(Long cartItemId, int quantity) {
        // check existing CartItem by id
        CartItem existingItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(()-> new ResourceNotFoundException(getMessage("cart-item-not-found")));

        existingItem.setQuantity(quantity);
        existingItem.setSubTotal(existingItem.getPrice().multiply(BigDecimal.valueOf(quantity)));

        CartItem changeItemQuantity =  cartItemRepository.save(existingItem);

        // update cart totals
        Cart cart = changeItemQuantity.getCart();
        cartService.updateCartTotals(cart);

        return mapToItemResponse(changeItemQuantity);
    }

    @Override
    public void delete(Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException(getMessage("cart-item-not-found")));
        cartItemRepository.delete(item);

        // update cart totals
        Cart cart = item.getCart();
        cartService.updateCartTotals(cart);
    }

    private CartItemResponse mapToItemResponse(CartItem entity) {
        return modelMapper.map(entity, CartItemResponse.class);
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
