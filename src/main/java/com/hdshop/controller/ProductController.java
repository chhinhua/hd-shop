package com.hdshop.controller;

import com.hdshop.dto.product.ProductDTO;
import com.hdshop.dto.product.ProductResponse;
import com.hdshop.entity.product.Product;
import com.hdshop.service.product.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Product")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;

    /**
     * Creates a new product via API endpoint.
     *
     * @param product The product to be created.
     * @return The created ProductDTO, or an error message handled by the exception handler.
     */
    @Operation(summary = "Create Product")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "Bear Authentication")
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody Product product) {
        ProductDTO saveProduct = productService.createProduct(product);
        return new ResponseEntity<>(saveProduct, HttpStatus.CREATED);
    }

    /**
     * Retrieves products with pagination.
     *
     * @param pageNo   The page number.
     * @param pageSize The size of each page.
     * @return A ProductResponse object containing products within the specified pagination.
     */
    @Operation(summary = "Get All Products", description = "Get all Products via REST API with pagination")
    @GetMapping
    public ResponseEntity<ProductResponse> getAllProduct(
            @RequestParam(value = "pageNo", required = false, defaultValue = "1") int pageNo,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(productService.getAllProducts(pageNo, pageSize));
    }

    /**
     * Retrieves a single product by its ID.
     *
     * @param productId The ID of the product to retrieve.
     * @return The productDTO corresponding to the given ID.
     */
    @Operation(summary = "Get a Single Product")
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getOne(@PathVariable(value = "id") Long productId) {
        return ResponseEntity.ok(productService.getOne(productId));
    }

    /**
     * Updates a product via API endpoint.
     *
     * @param product   The updated product data.
     * @param productId The ID of the product to be updated.
     * @return The updated ProductDTO.
     */
    @Operation(summary = "Update a Product")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "Bear Authentication")
    @PutMapping("{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @RequestBody ProductDTO product,
            @PathVariable(value = "id") Long productId) {
        return ResponseEntity.ok(productService.updateProduct(product, productId));
    }
}
