package com.duck.controller;

import com.duck.dto.product.AddInventoryRequest;
import com.duck.dto.product.ProductDTO;
import com.duck.dto.product.ProductResponse;
import com.duck.entity.Product;
import com.duck.service.product.ProductService;
import com.duck.service.product.ProductSkuService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Tag(name = "Product")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;
    private final ProductSkuService skuService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add-inventory")
    public ResponseEntity<?> addInventory(@RequestBody AddInventoryRequest request) {
        return ResponseEntity.ok(productService.addInventory(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/make-discount")
    public ResponseEntity<?> makeDiscount(@RequestParam(value = "product_id") Long product_id,
                                          @RequestParam(value = "percent_discount") int percent_discount) {
        productService.makeDiscount(product_id, percent_discount);
        return ResponseEntity.ok("Make discount successfully");
    }

    @PostMapping("/analyze")
    public ResponseEntity<?> productAnalysis(@RequestParam(value = "product_id") Long productId,
                                             @RequestParam(value = "type") String type) {
        productService.productAnalysis(productId, type);
        return ResponseEntity.ok("Analysis successfully");
    }

    @Operation(summary = "Create Product")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bear Authentication")
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody Product product) {
        ProductDTO saveProduct = productService.create(product);
        return new ResponseEntity<>(saveProduct, HttpStatus.CREATED);
    }

    @Operation(summary = "Get a Single Product")
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getOne(@PathVariable(value = "id") Long productId, Principal principal) {
        return ResponseEntity.ok(productService.getOne(productId, principal));
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
        return ResponseEntity.ok(productService.toggleActive(productId));
    }

    @Operation(summary = "Toggle Selling Status of a Product")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bear Authentication")
    @PutMapping("/{id}/selling")
    public ResponseEntity<ProductDTO> toggleSellingProduct(@PathVariable(value = "id") Long productId) {
        return ResponseEntity.ok(productService.toggleSelling(productId));
    }

    @GetMapping("/search")
    public ResponseEntity<ProductResponse> search(
            @RequestParam(name = "sell", required = false) Boolean sell,
            @RequestParam(name = "key", required = false) String key,
            @RequestParam(name = "cate", required = false) List<String> cateNames,
            @RequestParam(name = "sort", required = false) List<String> sortCriteria,
            @RequestParam(value = "pageNo", required = false,
                    defaultValue = "${paging.default.page-number}") int pageNo,
            @RequestParam(value = "pageSize", required = false,
                    defaultValue = "${paging.default.page-size}") int pageSize,
            Principal principal
    ) throws JsonProcessingException {
        String username = null;
        if (principal != null) {
            username = principal.getName();
        }
        ProductResponse searchResponse = productService.filterProducts(
                sell, key, cateNames, sortCriteria, pageNo, pageSize, username
        );
        return ResponseEntity.ok(searchResponse);
    }

    @GetMapping("/sku")
    public ResponseEntity<?> getSku(
            @RequestParam(value = "product_id") Long product_id,
            @RequestParam(value = "value_names") List<String> value_names
    ) {
        return ResponseEntity.ok(skuService.findBySku(product_id, value_names));
    }

    @SecurityRequirement(name = "Bear Authentication")
    @Operation(summary = "Add product quantity")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/add-quantity")
    public ResponseEntity<ProductDTO> addQuantity(
            @RequestParam(value = "productId") Long product_id,
            @RequestParam(value = "quantity") Integer quantity
    ) {
        return ResponseEntity.ok(productService.addQuantity(product_id, quantity));
    }
}
