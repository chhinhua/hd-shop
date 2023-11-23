package com.hdshop.controller;

import com.hdshop.dto.category.CategoryDTO;
import com.hdshop.dto.category.CategoryResponse;
import com.hdshop.dto.product.ProductResponse;
import com.hdshop.service.category.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
    private final MessageSource messageSource;

    public CategoryController(CategoryService categoryService, MessageSource messageSource) {
        this.categoryService = categoryService;
        this.messageSource = messageSource;
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
        String successMessage = messageSource.getMessage("deleted-successfully", null, LocaleContextHolder.getLocale());
        return ResponseEntity.ok(successMessage);
    }

    @Operation(summary = "Filter categories")
    @GetMapping("/search")
    public ResponseEntity<CategoryResponse> filter(
            @RequestParam(name = "key", required = false) String key,
            @RequestParam(name = "sort", required = false) List<String> sortCriteria,
            @RequestParam(value = "pageNo", required = false,
                    defaultValue = "${paging.default.page-number}") int pageNo,
            @RequestParam(value = "pageSize", required = false,
                    defaultValue = "${paging.default.page-size}") int pageSize

    ) {
        CategoryResponse filterCategories = categoryService.filter(
               key, sortCriteria, pageNo, pageSize
        );
        return ResponseEntity.ok(filterCategories);
    }
}
