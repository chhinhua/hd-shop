package com.hdshop.service.category;

import com.hdshop.dto.category.CategoryDTO;
import com.hdshop.dto.category.CategoryResponse;
import com.hdshop.entity.Category;

import java.util.List;

public interface CategoryService {
    Category findByName(final String cateName);

    CategoryDTO findByIdOrSlug(final String identifier);

    CategoryDTO create(final CategoryDTO categoryDTO);

    CategoryDTO update(final Long id, final CategoryDTO categoryDTO);

    void delete(final Long id);

    CategoryResponse getAll(int pageNo, int pageSize);

    CategoryResponse filter(
            String searchTerm,
            List<String> sortCriteria,
            int pageNo,
            int pageSize
    );
}
