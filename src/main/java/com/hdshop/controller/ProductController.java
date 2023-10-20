package com.hdshop.controller;

import com.hdshop.dto.product.CreateProductDTO;
import com.hdshop.dto.product.ProductDTO;
import com.hdshop.dto.product.ProductResponse;
import com.hdshop.entity.product.Product;
import com.hdshop.service.product.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Crate new product api endpoint
     * @date 12-10-2023
     * @param dto
     * @return ProductDTO is created or message error by exception handler
     */
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody CreateProductDTO dto) {
        ProductDTO saveProduct = productService.createProduct2(dto);
        return new ResponseEntity<>(saveProduct, HttpStatus.CREATED);
    }

    /**
     * Get products within pagination
     * @date 12-10-2023
     * @param pageNo
     * @param pageSize
     * @return ProductResponse object (products within pagination)
     */
    @GetMapping
    public ResponseEntity<ProductResponse> getAllProduct(@RequestParam(value = "pageNo", required = false, defaultValue = "1") int pageNo,
                                                         @RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(productService.getAllProducts(pageNo, pageSize));
    }
}
