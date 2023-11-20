package com.hdshop.controller;

import com.hdshop.dto.user.FollowProductDTO;
import com.hdshop.service.user.UserFollowProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@Tag(name = "Folowing")
@RequestMapping("/api/v1/follows")
public class FollowController {
    private final UserFollowProductService followService;

    public FollowController(UserFollowProductService followService) {
        this.followService = followService;
    }

    @Operation(summary = "Toggle follow")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "Bear Authentication")
    @PutMapping
    public ResponseEntity<?> create(@RequestParam(value = "productId") Long productId, Principal principal) {
        FollowProductDTO follow = followService.create(productId, principal);
        return new ResponseEntity<>(follow, HttpStatus.CREATED);
    }
}
