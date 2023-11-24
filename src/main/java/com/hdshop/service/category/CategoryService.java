package com.hdshop.service.category;

import com.hdshop.dto.category.CategoryDTO;
import com.hdshop.dto.category.CategoryResponse;
import com.hdshop.dto.product.ProductResponse;

import java.util.List;

public interface CategoryService {
    CategoryDTO getCategoryByIdOrSlug(final String identifier);

    CategoryDTO createCategory(final CategoryDTO categoryDTO);

    CategoryDTO updateCategory(final Long id, final CategoryDTO categoryDTO);

    void deleteCategory(final Long id);

    CategoryResponse getAll(int pageNo, int pageSize);

    CategoryResponse filter(
            String searchTerm,
            List<String> sortCriteria,
            int pageNo,
            int pageSize
    );
}
