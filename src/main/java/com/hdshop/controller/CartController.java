package com.hdshop.controller;

import com.hdshop.dto.cart.CartItemDTO;
import com.hdshop.entity.CartItem;
import com.hdshop.service.cart.CartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Tag(name = "Cart")
@RestController
@RequestMapping("/api/v1/carts")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping
    public ResponseEntity<CartItemDTO> addItemToCart(@Valid @RequestBody CartItemDTO cartItemDTO, Principal principal) {
        String username = principal.getName();

        Long cartId = cartService.getCartByUsername(username).getId();

        CartItemDTO addedItem = cartService.addToCart(cartId, cartItemDTO);

        return new ResponseEntity<>(addedItem, HttpStatus.CREATED);
    }
}
