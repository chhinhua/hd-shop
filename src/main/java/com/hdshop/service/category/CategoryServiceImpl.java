package com.hdshop.service.category;

import com.hdshop.component.UniqueSlugGenerator;
import com.hdshop.dto.category.CategoryDTO;
import com.hdshop.dto.category.CategoryResponse;
import com.hdshop.entity.Category;
import com.hdshop.exception.APIException;
import com.hdshop.exception.ResourceNotFoundException;
import com.hdshop.repository.CategoryRepository;
import com.hdshop.repository.ProductRepository;
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
    private final ProductRepository productRepository;

    /**
     * Get a single category by id or slug
     *
     * @param identifier (id or slug)
     * @return CategoryDTO instance
     */
    @Override
    public CategoryDTO findByIdOrSlug(String identifier) {
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
    public CategoryDTO create(CategoryDTO dto) {
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
        category.setIsDeleted(false);

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
    public CategoryDTO update(Long id, CategoryDTO dto) {
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
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(getMessage("category-not-found")));

        // set null for child cates
        if (category.getChildren() != null) {
            category.getChildren().forEach((child) -> {
                child.setParent(null);
                categoryRepository.save(child);
            });
        }

        // delete products by set isdelete product
        if (category.getProducts().size() > 0) {
            category.getProducts().forEach((product) -> {
                product.setIsActive(false);
                productRepository.save(product);
            });
        }

        category.setIsDeleted(true);
        categoryRepository.save(category);
    }

    @Override
    public CategoryResponse getAll(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

        Page<Category> catePage = categoryRepository.findAll(pageable);

        // get content for page object
        List<Category> cateList = catePage.getContent();

        List<CategoryDTO> content = cateList.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        // set data to the product response
        CategoryResponse cateResponse = new CategoryResponse();
        cateResponse.setContent(content);
        cateResponse.setPageNo(catePage.getNumber() + 1);
        cateResponse.setPageSize(catePage.getSize());
        cateResponse.setTotalPages(catePage.getTotalPages());
        cateResponse.setTotalElements(catePage.getTotalElements());
        cateResponse.setLast(catePage.isLast());

        return cateResponse;
    }

    @Override
    public CategoryResponse filter(String key, List<String> sortCriteria, int pageNo, int pageSize) {
        // follow Pageable instances
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

        Page<Category> catePage = categoryRepository.filter(key, sortCriteria, pageable);

        // get content for page object
        List<Category> cateList = catePage.getContent();

        List<CategoryDTO> content = cateList.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        // set data to the product response
        CategoryResponse cateResponse = new CategoryResponse();
        cateResponse.setContent(content);
        cateResponse.setPageNo(catePage.getNumber() + 1);
        cateResponse.setPageSize(catePage.getSize());
        cateResponse.setTotalPages(catePage.getTotalPages());
        cateResponse.setTotalElements(catePage.getTotalElements());
        cateResponse.setLast(catePage.isLast());

        return cateResponse;
    }

    @Override
    public Category findByName(String cateName) {
        return categoryRepository.findByName(cateName).orElseThrow(() ->
                new ResourceNotFoundException(getMessage("category-not-found"))
        );
    }

    /**
     * Convert Category DTO to  Category entity class
     *
     * @param category
     * @return CategoryDTO object
     */
    private CategoryDTO mapToDTO(Category category) {
        CategoryDTO dto = modelMapper.map(category, CategoryDTO.class);
        long productCount = category.getProducts().size();

        if (category.getChildren() != null && category.getChildren().size() > 0) {
            productCount = category.getChildren().stream().mapToLong(child -> child.getProducts().size()).sum();
        }
        dto.setProductNumber(productCount);

        if (category.getParent() != null) {
            dto.setParentName(category.getParent().getName());
        }
        return dto;
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
