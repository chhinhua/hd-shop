package com.hdshop.services.category;

import com.hdshop.dtos.CategoryDTO;
import com.hdshop.entities.Category;
import com.hdshop.exceptions.ResourceNotFoundException;
import com.hdshop.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Query all categories
     * @param
     * @return list CategoryDTO
     */
    @Override
    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories
                .stream()
                .map((category) -> modelMapper.map(category, CategoryDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Query Category by id
     * @param id
     * @return a CategoryDTO
     */
    @Override
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return modelMapper.map(category, CategoryDTO.class);
    }
}
