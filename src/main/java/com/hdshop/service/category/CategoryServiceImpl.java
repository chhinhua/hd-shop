package com.hdshop.service.category;

import com.hdshop.component.UniqueSlugGenerator;
import com.hdshop.dto.category.CategoryDTO;
import com.hdshop.dto.category.CategoryResponse;
import com.hdshop.entity.Category;
import com.hdshop.exception.APIException;
import com.hdshop.exception.ResourceNotFoundException;
import com.hdshop.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final UniqueSlugGenerator slugGenerator;
    private final MessageSource messageSource;

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
                    .orElseThrow(() -> new ResourceNotFoundException(getMessage("category-not-found")));
        } catch (NumberFormatException e) {
            category = categoryRepository.findBySlug(identifier.trim())
                    .orElseThrow(() -> new ResourceNotFoundException(getMessage("category-not-found")));
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
            throw new APIException(HttpStatus.BAD_REQUEST, getMessage("category-name-already-exists"));
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
                .orElseThrow(() -> new ResourceNotFoundException(getMessage("category-not-found")));

        // validate existing categoryDTO name
        if (!category.getName().equals(categoryDTO.getName()) && categoryRepository.existsCategoryByName(categoryDTO.getName())) {
            throw new APIException(HttpStatus.BAD_REQUEST, getMessage("category-name-already-exists"));
        }

        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());

        String uniqueSlug = slugGenerator.generateUniqueSlug(category, category.getName());
        category.setSlug(uniqueSlug);

        setParentById(categoryDTO.getParentId(), category);

        Category updateCategory = categoryRepository.save(category);

        return mapToDTO(updateCategory);
    }

    /**
     * Delete category by id
     * @param id
     */
    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(getMessage("category-not-found")));

        if (category.getChildren() != null) {
            for (Category child : category.getChildren()) {
                child.setParent(null);
                categoryRepository.save(child);
            }
        }
        categoryRepository.delete(category);
    }

    /**
     * Retrieves a paginated list of categories.
     *
     * @param pageNo   The page number (1-based).
     * @param pageSize The number of items per page.
     * @return A CategoryResponse object containing the paginated category data.
     */
    @Override
    public CategoryResponse getAllCategories(int pageNo, int pageSize) {
        // follow Pageable instances
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

        Page<Category> categoryPage = categoryRepository.findAll(pageable);

        // get content for page object
        List<Category> categoryList = categoryPage.getContent();

        List<CategoryDTO> content = categoryList.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        // set data to the category response
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(content);
        categoryResponse.setPageNo(categoryPage.getNumber() + 1);
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setLast(categoryPage.isLast());

        return categoryResponse;
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

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
