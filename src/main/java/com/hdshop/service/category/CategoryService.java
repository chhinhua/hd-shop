package com.hdshop.service.category;

import com.hdshop.dto.CategoryDTO;

import java.util.List;

public interface CategoryService {
    List<CategoryDTO> getAllCategories();

    CategoryDTO getCategoryByIdOrSlug(final String identifier);

    CategoryDTO createCategory(final CategoryDTO categoryDTO);

    CategoryDTO updateCategory(final Long id, final CategoryDTO categoryDTO);

    void deleteCategory(final Long id);
}
