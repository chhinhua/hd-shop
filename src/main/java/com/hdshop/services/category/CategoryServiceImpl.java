package com.hdshop.services.category;

import com.github.slugify.Slugify;
import com.hdshop.dtos.CategoryDTO;
import com.hdshop.entities.Category;
import com.hdshop.exceptions.APIException;
import com.hdshop.exceptions.ResourceNotFoundException;
import com.hdshop.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final Slugify slugify;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper, Slugify slugify) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
        this.slugify = slugify;
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
    public CategoryDTO getCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return modelMapper.map(category, CategoryDTO.class);
    }

    /**
     * Create a new category
     * @param categoryDTO
     * @return categoryDTO instance
     */
    @Override
    public CategoryDTO addCategory(CategoryDTO categoryDTO) {
        // check category name exists in database
        if (categoryRepository.existsCategoryByName(categoryDTO.getName())) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Category name is already exists");
        }

        categoryDTO.setSlug(slugify.slugify(categoryDTO.getName()));
        categoryDTO.setCreateAt(new Date());
        categoryDTO.setUpdateAt(new Date());

        Category category = mapToEntity(categoryDTO);
        Category saveCategory = categoryRepository.save(category);

        return mapToDTO(saveCategory);
    }

    /**
     * Update a category
     * @param id
     * @param categoryDTO
     * @return categoryDTO instance have been updated
     */
    @Override
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {

        return null;
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        categoryRepository.delete(category);
    }

    /**
     * Convert Category DTO to  Category entity class
     * @param categoryDTO
     * @return Category entity
     */
    private Category mapToEntity(CategoryDTO categoryDTO) {
        return modelMapper.map(categoryDTO, Category.class);
    }

    private CategoryDTO mapToDTO(Category category) {
        return modelMapper.map(category, CategoryDTO.class);
    }
}
