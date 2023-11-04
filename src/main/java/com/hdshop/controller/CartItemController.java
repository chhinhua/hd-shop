package com.hdshop.controller;

import com.hdshop.dto.cart.CartItemResponse;
import com.hdshop.service.cart.CartItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Cart item")
@RestController
@SecurityRequirement(name = "Bear Authentication")
@RequestMapping("/api/v1/cart/items")
public class CartItemController {
    private final CartItemService cartItemService;

    public CartItemController(CartItemService cartItemService) {
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
        cartItemService.deleteOneCartItem(cartItemId);
        return ResponseEntity.ok("Cart item deleted successfully");
    }
}

