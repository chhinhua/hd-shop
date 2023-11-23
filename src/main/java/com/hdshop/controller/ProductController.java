package com.hdshop.controller;

import com.hdshop.dto.product.ProductDTO;
import com.hdshop.dto.product.ProductResponse;
import com.hdshop.dto.product.RequestSku;
import com.hdshop.entity.Product;
import com.hdshop.entity.ProductSku;
import com.hdshop.service.product.ProductService;
import com.hdshop.service.product.ProductSkuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Product")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;
    private final ProductSkuService skuService;

    @Operation(summary = "Create Product")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bear Authentication")
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody Product product) {
        ProductDTO saveProduct = productService.create(product);
        return new ResponseEntity<>(saveProduct, HttpStatus.CREATED);
    }

    @Operation(summary = "Get All Products", description = "Get all Products via REST API with pagination (active product)")
    @GetMapping
    public ResponseEntity<ProductResponse> getAllActiveProduct(
            @RequestParam(value = "pageNo", required = false,
                    defaultValue = "${paging.default.page-number}") int pageNo,
            @RequestParam(value = "pageSize", required = false,
                    defaultValue = "${paging.default.page-size}") int pageSize
    ) {
        return ResponseEntity.ok(productService.getAllIsActive(pageNo, pageSize));
    }

    @Operation(summary = "Get a Single Product")
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getOne(@PathVariable(value = "id") Long productId) {
        return ResponseEntity.ok(productService.getOne(productId));
    }

    @Operation(summary = "Update a Product")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bear Authentication")
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @RequestBody ProductDTO product,
            @PathVariable(value = "id") Long productId) {
        return ResponseEntity.ok(productService.update(product, productId));
    }

    @Operation(summary = "Toggle Active Status of a Product")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bear Authentication")
    @PutMapping("/{id}/active")
    public ResponseEntity<ProductDTO> toggleActiveProduct(@PathVariable(value = "id") Long productId) {
        return ResponseEntity.ok(productService.toggleActiveStatus(productId));
    }

    @Operation(summary = "Toggle Selling Status of a Product")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bear Authentication")
    @PutMapping("/{id}/selling")
    public ResponseEntity<ProductDTO> toggleSellingProduct(@PathVariable(value = "id") Long productId) {
        return ResponseEntity.ok(productService.toggleSellingStatus(productId));
    }

    @GetMapping("/search")
    public ResponseEntity<ProductResponse> searchProducts(
            @RequestParam(name = "key", required = false) String key,
            @RequestParam(name = "cate", required = false) List<String> cateNames,
            @RequestParam(name = "sort", required = false) List<String> sortCriteria,
            @RequestParam(value = "pageNo", required = false,
                    defaultValue = "${paging.default.page-number}") int pageNo,
            @RequestParam(value = "pageSize", required = false,
                    defaultValue = "${paging.default.page-size}") int pageSize
    ) {
        ProductResponse searchResponse = productService.searchSortAndFilterProducts(
                key, cateNames, sortCriteria, pageNo, pageSize
        );
        return ResponseEntity.ok(searchResponse);
    }

    @GetMapping("/sku")
    public ResponseEntity<?> getSkuPrice(@RequestBody RequestSku reqSku) {
        ProductSku sku = skuService.findByProductIdAndValueNames(
                reqSku.getProductId(), reqSku.getValueNames()
        );
        return ResponseEntity.ok(sku.getPrice());
    }
}
