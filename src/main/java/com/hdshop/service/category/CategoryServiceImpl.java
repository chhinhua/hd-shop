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
     *
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
     * @param dto
     * @return categoryDTO instance
     */
    @Override
    public CategoryDTO createCategory(CategoryDTO dto) {
        // check category name exists in database
        if (categoryRepository.existsCategoryByName(dto.getName())) {
            throw new APIException(HttpStatus.BAD_REQUEST, getMessage("category-name-already-exists"));
        }

        // retrieve parent category from parentName
        Category parentCategory = categoryRepository.findByName(dto.getParentName()).orElse(null);

        // build category
        Category category = new Category();
        category.setParent(parentCategory);
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        String uniqueSlug = slugGenerator.generateUniqueSlug(category, category.getName());
        category.setSlug(uniqueSlug);

        Category newCategory = categoryRepository.save(category);

        return mapToDTO(newCategory);
    }

    /**
     * Update a category
     *
     * @param id
     * @param dto Category DTO
     * @return categoryDTO instance have been updated
     */
    @Override
    public CategoryDTO updateCategory(Long id, CategoryDTO dto) {
        // check existing category by id
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(getMessage("category-not-found")));

        // validate existing dto name
        if (!category.getName().equals(dto.getName()) &&
                categoryRepository.existsCategoryByName(dto.getName())
        ) {
            throw new APIException(HttpStatus.BAD_REQUEST,
                    getMessage("category-name-already-exists")
            );
        }

        Category parentCategory = categoryRepository.findByName(dto.getParentName()).orElse(null);
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        String uniqueSlug = slugGenerator.generateUniqueSlug(category, category.getName());
        category.setSlug(uniqueSlug);
        category.setParent(parentCategory);

        Category updateCategory = categoryRepository.save(category);

        return mapToDTO(updateCategory);
    }

    /**
     * Delete category by id
     *
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
     * Convert Category DTO to  Category entity class
     *
     * @param category
     * @return CategoryDTO object
     */
    private CategoryDTO mapToDTO(Category category) {
        CategoryDTO dto = modelMapper.map(category, CategoryDTO.class);
        dto.setProductNumber((long) category.getProducts().size());
        if (category.getParent() != null) {
            dto.setParentName(category.getParent().getName());
        }
        return dto;
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
