package com.duck.service.category;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.duck.dto.category.CategoryDTO;
import com.duck.dto.category.CategoryResponse;
import com.duck.entity.Category;

import java.util.List;

public interface CategoryService {
    Category findByName(final String cateName);
    Category findById(final Long id);

    CategoryDTO findByIdOrSlug(final String identifier);

    CategoryDTO create(final CategoryDTO categoryDTO);

    CategoryDTO update(final Long id, final CategoryDTO categoryDTO);

    void delete(final Long id);

    CategoryResponse getAll(int pageNo, int pageSize) throws JsonProcessingException;

    CategoryResponse filter(
            String searchTerm,
            List<String> sortCriteria,
            int pageNo,
            int pageSize
    ) throws JsonProcessingException;
}
