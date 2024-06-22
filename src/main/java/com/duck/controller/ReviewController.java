package com.duck.controller;

import com.duck.dto.review.ReviewDTO;
import com.duck.dto.review.ReviewResponse;
import com.duck.service.review.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "Get all reviews for product")
    @GetMapping("/{product_id}")
    public ResponseEntity<ReviewResponse> getProductReviews(
            @PathVariable(value = "product_id") Long product_id,
            @RequestParam(name = "star", required = false) Integer star,
            @RequestParam(value = "pageNo", required = false, defaultValue = "1") int pageNo,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize
    ) {
        return ResponseEntity.ok(reviewService.getProductReviews(product_id, star, pageNo, pageSize));
    }
}
