package com.hdshop.controller;

import com.hdshop.dto.cart.CartItemDTO;
import com.hdshop.dto.cart.CartItemResponse;
import com.hdshop.service.cart.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Tag(name = "Cart")
@RestController
@RequestMapping("/api/v1/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    //@PreAuthorize("hasRole('USER')")
    @Operation(summary = "Add to cart")
    @PostMapping
    public ResponseEntity<CartItemResponse> addItemToCart(@Valid @RequestBody CartItemDTO cartItemDTO, Principal principal) {
        String username = principal.getName();

        Long cartId = cartService.getCartByUsername(username).getId();

        CartItemResponse addedItem = cartService.addToCart(cartId, cartItemDTO);

        return new ResponseEntity<>(addedItem, HttpStatus.CREATED);
    }


    //@PreAuthorize("hasRole('USER')")
    @Operation(summary = "Change cart-item quantity")
    @PutMapping("/item/{itemId}")
    public ResponseEntity<CartItemResponse> changeItemQuantity(@PathVariable(value = "itemId") Long cartItemId,
                                                          @RequestParam int quantity) {
        CartItemResponse changeItem = cartService.changeQuantity(cartItemId, quantity);

        return ResponseEntity.ok(changeItem);
    }
}
