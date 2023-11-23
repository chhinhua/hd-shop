package com.hdshop.controller;

import com.hdshop.dto.review.ReviewDTO;
import com.hdshop.service.review.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Tag(name = "Review product")
@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @SecurityRequirement(name = "Bear Authentication")
    @Operation(summary = "Add new review")
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<?> review(@Valid @RequestBody ReviewDTO dto, Principal principal) {
        return new ResponseEntity<>(reviewService.create(dto, principal), HttpStatus.CREATED);
    }
}
