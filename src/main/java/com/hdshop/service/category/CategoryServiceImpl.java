package com.hdshop.service.category;

import com.github.slugify.Slugify;
import com.hdshop.dto.CategoryDTO;
import com.hdshop.entity.Category;
import com.hdshop.exception.APIException;
import com.hdshop.exception.ResourceNotFoundException;
import com.hdshop.repository.CategoryRepository;
import com.hdshop.component.UniqueSlugGenerator;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final Slugify slugify;
    private final UniqueSlugGenerator slugGenerator;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper, Slugify slugify, UniqueSlugGenerator slugGenerator) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
        this.slugify = slugify;
        this.slugGenerator = slugGenerator;
    }

    /**
     * Query all categories
     *
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
     * Get a single category by id or slug
     * @param identifier (id or slug)
     * @return CategoryDTO instance
     */
    @Override
    public CategoryDTO getCategoryByIdOrSlug(String identifier) {
        Category category;
        try {
            Long id = Long.parseLong(identifier.trim());
            category = categoryRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        } catch (NumberFormatException e) {
            category = categoryRepository.findBySlug(identifier.trim())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "slug", identifier));
        }

        return mapToDTO(category);
    }


    /**
     * Create new category
     *
     * @param categoryDTO
     * @return categoryDTO instance
     */
    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        // check category name exists in database
        if (categoryRepository.existsCategoryByName(categoryDTO.getName())) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Category name is already exists");
        }

        Category category = mapToEntity(categoryDTO);

        String uniqueSlug = slugGenerator.generateUniqueSlug(category, category.getName());
        category.setSlug(uniqueSlug);
        setParentById(categoryDTO.getParentId(), category);

        Category newCategory = categoryRepository.save(category);

        return mapToDTO(newCategory);
    }

    /**
     * Update a category
     * @param id
     * @param categoryDTO
     * @return categoryDTO instance have been updated
     */
    @Override
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        // check existing category by id
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        // validate existing categoryDTO name
        if (!category.getName().equals(categoryDTO.getName()) && categoryRepository.existsCategoryByName(categoryDTO.getName())) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Category name already exists");
        }

        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        setParentById(categoryDTO.getParentId(), category);

        String uniqueSlug = slugGenerator.generateUniqueSlug(category, category.getName());
        category.setSlug(uniqueSlug);


        Category updateCategory = categoryRepository.save(category);

        return mapToDTO(updateCategory);
    }

    /**
     * Set the unique slug
     * @param category
     */
    private void setUniqueSlug(Category category) {
        String uniqueSlug = slugGenerator.generateUniqueCategorySlug(slugify.slugify(category.getName()));
        category.setSlug(uniqueSlug);
    }

    /**
     * Set the category parent for category from the categoryDTO_id
     * @param id
     * @param category
     */
    private void setParentById(Long id, Category category) {
        Optional<Category> parentCategory = id != null
                ? categoryRepository.findById(id)
                : Optional.empty();

        category.setParent(parentCategory.orElse(null));
    }

    /**
     * Delete category by id
     * @param id
     */
    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        categoryRepository.delete(category);
    }

    /**
     * Convert Category DTO to  Category entity class
     *
     * @param categoryDTO
     * @return Category entity object
     */
    private Category mapToEntity(CategoryDTO categoryDTO) {
        return modelMapper.map(categoryDTO, Category.class);
    }

    /**
     * Convert Category DTO to  Category entity class
     *
     * @param category
     * @return CategoryDTO object
     */
    private CategoryDTO mapToDTO(Category category) {
        return modelMapper.map(category, CategoryDTO.class);
    }
}
