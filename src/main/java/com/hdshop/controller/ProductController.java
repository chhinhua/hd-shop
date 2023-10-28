package com.hdshop.controller;

import com.hdshop.dto.product.ProductDTO;
import com.hdshop.dto.product.ProductResponse;
import com.hdshop.entity.product.Product;
import com.hdshop.service.product.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Product")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;

    /**
     * Crate new product api endpoint
     * @date 12-10-2023
     * @param product
     * @return ProductDTO is created or message error by exception handler
     */
    @Operation(summary = "Create Product")
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody Product product) {
        ProductDTO saveProduct = productService.createProduct(product);
        return new ResponseEntity<>(saveProduct, HttpStatus.CREATED);
    }

    /**
     * Get products within pagination
     * @date 12-10-2023
     * @param pageNo
     * @param pageSize
     * @return ProductResponse object (products within pagination)
     */
    @Operation(summary = "Get All Product", description = "Get all Product REST API within pagination")
    @GetMapping
    public ResponseEntity<ProductResponse> getAllProduct(@RequestParam(value = "pageNo", required = false, defaultValue = "1") int pageNo,
                                                         @RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(productService.getAllProducts(pageNo, pageSize));
    }

    /**
     * Get one product by id
     * @param productId
     * @return productDTO
     */
    @Operation(summary = "Get a single Product")
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getOne(@PathVariable(value = "id") Long productId) {
        return ResponseEntity.ok(productService.getOne(productId));
    }

    @Operation(summary = "Update a product")
    @PutMapping("{id}")
    public ResponseEntity<ProductDTO> updateProduct(@RequestBody ProductDTO product,
                                                    @PathVariable(value = "id") Long productId) {
        return ResponseEntity.ok(productService.updateProduct(product, productId));
    }
}
