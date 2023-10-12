package com.hdshop.controllers;

import com.hdshop.dtos.CategoryDTO;
import com.hdshop.services.category.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<CategoryDTO> getSingleCategory(@PathVariable String identifier) {
        return ResponseEntity.ok(categoryService.getCategoryByIdOrSlug(identifier));
    }

    @PostMapping
    public ResponseEntity<CategoryDTO> addNewCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO saveCategory = categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(saveCategory, HttpStatus.CREATED);
    }

    @PostMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id,
                                                      @Valid @RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Category deleted successfully");
    }
}
