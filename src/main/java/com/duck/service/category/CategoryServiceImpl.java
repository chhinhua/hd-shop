package com.duck.service.category;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.duck.component.UniqueSlugGenerator;
import com.duck.dto.category.CategoryDTO;
import com.duck.dto.category.CategoryResponse;
import com.duck.entity.Category;
import com.duck.exception.APIException;
import com.duck.exception.ResourceNotFoundException;
import com.duck.repository.CategoryRepository;
import com.duck.repository.ProductRepository;
import com.duck.service.redis.RedisService;
import com.duck.utils.AppUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryServiceImpl implements CategoryService {
    CategoryRepository categoryRepository;
    ModelMapper modelMapper;
    UniqueSlugGenerator slugGenerator;
    MessageSource messageSource;
    ProductRepository productRepository;
    RedisService<Category> redisService;
    static Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

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
    @Transactional
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
    @Transactional
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
    @Transactional
    public void delete(Long id) {
        Category category = findById(id);
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
    public CategoryResponse getAll(int pageNo, int pageSize) throws JsonProcessingException {
        logger.info(String.format("page_no=%d, page_size=%d", pageNo, pageSize));
        String redisKey = redisService.getKeyFrom(AppUtils.KEY_PREFIX_GET_ALL_CATEGORY, pageNo, pageSize);
        CategoryResponse response = redisService.getAll(redisKey, CategoryResponse.class);
        if (response != null && !response.getContent().isEmpty()) {
            return response;
        }

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<Category> catePage = categoryRepository.findAll(pageable);
        // get content for page object
        List<Category> cateList = catePage.getContent();
        List<CategoryDTO> content = cateList.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        CategoryResponse cateResponse = new CategoryResponse();
        cateResponse.setContent(content);
        cateResponse.setPageNo(catePage.getNumber() + 1);
        cateResponse.setPageSize(catePage.getSize());
        cateResponse.setTotalPages(catePage.getTotalPages());
        cateResponse.setTotalElements(catePage.getTotalElements());
        cateResponse.setLast(catePage.isLast());

        redisService.saveAll(redisKey, cateResponse); // save cache data
        return cateResponse;
    }

    @Override
    public CategoryResponse filter(String key, List<String> sortCriteria, int pageNo, int pageSize) throws JsonProcessingException {
        // retrieve and return cache data if exists
        logger.info(String.format("key=%s, sort=%s, page_no=%d, page_size=%d", key, sortCriteria, pageNo, pageSize));
        String redisKey = redisService.getKeyFrom(AppUtils.KEY_PREFIX_GET_ALL_CATEGORY, key, sortCriteria, pageNo, pageSize);
        CategoryResponse response = redisService.getAll(redisKey, CategoryResponse.class);
        if (response != null && !response.getContent().isEmpty()) {
            return response;
        }

        // retrieve data from database
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<Category> catePage = categoryRepository.filter(key, sortCriteria, pageable);
        List<CategoryDTO> content = catePage.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        CategoryResponse cateResponse = CategoryResponse.builder()
                .content(content)
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(catePage.getTotalPages())
                .totalElements(catePage.getTotalElements())
                .last(catePage.isLast())
                .build();

        redisService.saveAll(key, cateResponse); // caching data if not saved yet
        return cateResponse;
    }

    @Override
    public Category findByName(String cateName) {
        return categoryRepository.findByName(cateName).orElseThrow(() ->
                new ResourceNotFoundException(getMessage("category-not-found"))
        );
    }

    @Override
    public Category findById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() ->
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
