package com.hdshop.controller;

import com.hdshop.dto.category.CategoryDTO;
import com.hdshop.dto.category.CategoryResponse;
import com.hdshop.dto.product.ProductResponse;
import com.hdshop.service.category.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Category")
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Get All Categories", description = "Get all Categories via REST API with pagination")
    @GetMapping
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(value = "pageNo", required = false, defaultValue = "1") int pageNo,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize
    ) {
        return ResponseEntity.ok(categoryService.getAllCategories(pageNo, pageSize));
    }

    @Operation(summary = "Get single categories")
    @GetMapping("/{identifier}")
    public ResponseEntity<CategoryDTO> getSingleCategory(@PathVariable String identifier) {
        return ResponseEntity.ok(categoryService.getCategoryByIdOrSlug(identifier));
    }

    @Operation(summary = "Add new category")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bear Authentication")
    @PostMapping
    public ResponseEntity<CategoryDTO> addNewCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO saveCategory = categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(saveCategory, HttpStatus.CREATED);
    }

    @Operation(summary = "Update a category by id")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bear Authentication")
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id,
                                                      @Valid @RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryDTO));
    }

    @Operation(summary = "Delete a category by id")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bear Authentication")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Category deleted successfully");
    }
}
