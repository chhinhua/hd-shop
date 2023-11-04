package com.hdshop.controller;

import com.hdshop.dto.cart.CartItemDTO;
import com.hdshop.dto.cart.CartItemResponse;
import com.hdshop.dto.cart.CartResponse;
import com.hdshop.service.cart.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Tag(name = "Cart")
@RestController
@SecurityRequirement(name = "Bear Authentication")
@PreAuthorize("hasRole('USER')")
@RequestMapping("/api/v1/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @Operation(summary = "Add to cart")
    @PostMapping
    public ResponseEntity<CartItemResponse> addItemToCart(@Valid @RequestBody CartItemDTO cartItemDTO, Principal principal) {
        String username = principal.getName();

        Long cartId = cartService.getCartByUsername(username).getId();

        CartItemResponse addedItem = cartService.addToCart(cartId, cartItemDTO);

        return new ResponseEntity<>(addedItem, HttpStatus.CREATED);
    }

    @Operation(summary = "Get Cart by id")
    @GetMapping("/{cartId}")
    public ResponseEntity<CartResponse> getCart(@PathVariable Long cartId) {
        return ResponseEntity.ok(cartService.getCartById(cartId));
    }

    @Operation(summary = "Clear cart items")
    @DeleteMapping("/clear")
    public ResponseEntity<CartResponse> clearItems(Principal principal) {
        String username = principal.getName();
        return ResponseEntity.ok(cartService.clearItems(username));
    }

    @Operation(summary = "Clear cart items")
    @DeleteMapping("/remove")
    public ResponseEntity<CartResponse> removeListItems(@RequestBody List<Long> itemIds, Principal principal) {
        String username = principal.getName();
        return ResponseEntity.ok(cartService.removeListItems(username, itemIds));
    }

}
