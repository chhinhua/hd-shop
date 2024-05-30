package com.hdshop.controller;

import com.hdshop.dto.cart.CartItemResponse;
import com.hdshop.service.cart.CartItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Cart item")
@RestController
@SecurityRequirement(name = "Bear Authentication")
@RequestMapping("/api/v1/cart/items")
public class CartItemController {
    private final CartItemService cartItemService;
    private final MessageSource messageSource;

    public CartItemController(MessageSource messageSource, CartItemService cartItemService) {
        this.messageSource = messageSource;
        this.cartItemService = cartItemService;
    }

    @Operation(summary = "Change Cart item quantity")
    @PutMapping("/{itemId}")
    public ResponseEntity<CartItemResponse> changeItemQuantity(@PathVariable(value = "itemId") Long cartItemId,
                                                               @RequestParam int quantity) {
        CartItemResponse changeItem = cartItemService.changeQuantity(cartItemId, quantity);

        return ResponseEntity.ok(changeItem);
    }

    @Operation(summary = "Delete one Cart item")
    @DeleteMapping("/{itemId}")
    public ResponseEntity<String> deteleOneCartItem(@PathVariable(value = "itemId") Long cartItemId) {
        cartItemService.delete(cartItemId);
        String successMessage = messageSource.getMessage("deleted-successfully", null, LocaleContextHolder.getLocale());
        return ResponseEntity.ok(successMessage);
    }
}

