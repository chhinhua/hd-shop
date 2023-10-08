package com.hdshop.services.category;

import com.hdshop.dtos.CategoryDTO;

import java.util.List;

public interface CategoryService {
    List<CategoryDTO> getAllCategories();

    CategoryDTO getCategory(final Long id);

    CategoryDTO addCategory(final CategoryDTO categoryDTO);

    CategoryDTO updateCategory(final Long id, final CategoryDTO categoryDTO);

    void deleteCategory(final Long id);
}
