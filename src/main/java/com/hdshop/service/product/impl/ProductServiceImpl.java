package com.hdshop.service.product.impl;

import com.github.slugify.Slugify;
import com.hdshop.component.UniqueSlugGenerator;
import com.hdshop.dto.product.OptionDTO;
import com.hdshop.dto.product.ProductDTO;
import com.hdshop.dto.product.ProductResponse;
import com.hdshop.dto.product.ProductSkuDTO;
import com.hdshop.entity.*;
import com.hdshop.exception.InvalidException;
import com.hdshop.exception.ResourceNotFoundException;
import com.hdshop.repository.*;
import com.hdshop.service.category.CategoryService;
import com.hdshop.service.product.OptionService;
import com.hdshop.service.product.ProductService;
import com.hdshop.service.product.ProductSkuService;
import com.hdshop.validator.ProductValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final OptionValueRepository valueRepository;
    private final CategoryRepository categoryRepository;
    private final FollowRepository followRepository;
    private final ProductSkuRepository skuRepository;
    private final OptionRepository optionRepository;
    private final ProductSkuService productSkuService;
    private final CategoryService categoryService;
    private final OptionService optionService;
    private final UniqueSlugGenerator slugGenerator;
    private final ProductValidator productValidator;
    private final MessageSource messageSource;
    private final ModelMapper modelMapper;
    private final Slugify slugify;

    /**
     * Create a new product.
     *
     * @param product The product object to follow.
     * @return ProductDTO representing the created product.
     * @throws ResourceNotFoundException if the corresponding category is not found.
     */
    @Override
    @Transactional
    public ProductDTO create(Product product) {
        // validate input product
        productValidator.validate(product);

        // find the product category based on its ID
        Category category = categoryService.findByName(product.getCategory().getName());

        // build product
        String uniqueSlug = slugGenerator.generateUniqueProductSlug(slugify.slugify(product.getName()));
        product.setSlug(uniqueSlug);
        product.setCategory(category);
        product.setIsSelling(false);
        product.setIsActive(true);
        product.setSold(0);
        product.setRating(0f);
        product.setFavoriteCount(0);
        product.setNumberOfRatings(0);
        product.setQuantityAvailable(product.getQuantity());
        setProductForChildEntity(product);

        // normalize product information
        Product normalizedProduct = normalizeProduct(product);

        // save the product to the database
        Product newProduct = productRepository.save(normalizedProduct);

        // save information about product variants (productSkus)
        productSkuService.saveSkusFromProduct(newProduct);

        // convert the product to a ProductDTO object and return it
        return mapToDTO(findById(newProduct.getProductId()));
    }

    /**
     * Get all products within pagination.
     *
     * @param pageNo   Page number.
     * @param pageSize Number of items per page.
     * @return List of paginated products.
     */
    @Override
    public ProductResponse getAllIsActive(int pageNo, int pageSize) {
        // follow Pageable instances
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

        Page<Product> productPage = productRepository.findRandomProducts(pageable);

        // get content for page object
        List<Product> productList = productPage.getContent();

        List<ProductDTO> content = productList.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        // set data to the product response
        ProductResponse productResponse = new ProductResponse();
        long totalElements = productPage.getTotalElements();

        productResponse.setContent(content);
        productResponse.setPageNo(productPage.getNumber() + 1);
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setLast(productPage.isLast());

        long lastPageSize = totalElements % pageSize != 0 ?
                totalElements % pageSize : totalElements != 0 ?
                pageSize : 0;
        productResponse.setLastPageSize(lastPageSize);

        return productResponse;
    }

    /**
     * Get a single product.
     *
     * @param productId Product ID.
     * @return Product DTO object.
     * @throws ResourceNotFoundException if the product is not found.
     */
    @Override
    public ProductDTO getOne(Long productId, Principal principal) {
        Product product = findById(productId);
        ProductDTO dto = mapToDTO(product);

        if (principal == null) {
            return dto;
        }
        boolean isLiked = followRepository
                .existsByProduct_ProductIdAndUser_UsernameAndIsDeletedFalse(dto.getId(), principal.getName());
        dto.setLiked(isLiked);
        return dto;
    }

    @Override
    public Product findById(Long productId) {
        return productRepository.findById(productId).orElseThrow(() ->
                new ResourceNotFoundException(getMessage("product-not-found"))
        );
    }

    /**
     * Update a product.
     *
     * @param dto Updated product information.
     * @param productId  Product ID to update.
     * @return Updated product DTO object.
     * @date 25-10-2023
     */
    @Override
    @Transactional
    public ProductDTO update(ProductDTO dto, Long productId) {
        // validate product update data
        productValidator.validateUpdate(dto);

        // Kiểm tra nếu sản phẩm đã tồn tại
        Product existingProduct = findById(productId);

        // Kiểm tra nếu danh mục đã tồn tại
        Category category = categoryService.findByName(dto.getCategory().getName());

        // Cập nhật các trường thay đổi
        setProductFields(dto, existingProduct, category);

        // Chuẩn hóa dữ liệu sản phẩm đầu vào
        Product normalizedProduct = normalizeProduct(existingProduct);

        // Lưu sản phẩm đã cập nhật
        existingProduct = productRepository.save(normalizedProduct);

        // Lưu hoặc cập nhật Options và OptionValues
        saveOrUpdateOptions(existingProduct, dto.getOptions());

        // Lưu hoặc cập nhật ProductSkus
        saveOrUpdateSkus(existingProduct, dto.getSkus());

        // Trả về DTO của sản phẩm sau khi cập nhật
        return mapToDTO(findById(productId));
    }

    private void updateOptionsAndValues(Product product, List<Option> options) {
        List<Option> existingOptions = product.getOptions();

        for (Option existingOption : existingOptions) {
            boolean found = false;
            for (Option updatedOption : options) {
                if (existingOption.getOptionName().equals(updatedOption.getOptionName())) {
                    found = true;

                    // Update Option's details
                    existingOption.setOptionName(updatedOption.getOptionName());

                    // Update or create OptionValues
                    updateOrCreateOptionValues(existingOption, updatedOption.getValues());

                    break;
                }
            }

            // If Option not found in updated list, remove it
            if (!found) {
                product.getOptions().remove(existingOption);
                optionRepository.delete(existingOption);
            }
        }

        // Save or create new Options
        for (Option option : options) {
            if (!existingOptions.contains(option)) {
                Option persistedOption = optionRepository.save(option);
                product.getOptions().add(persistedOption);

                // Save or create OptionValues for new Options
                updateOrCreateOptionValues(persistedOption, option.getValues());
            }
        }
    }

    private void updateOrCreateOptionValues(Option option, List<OptionValue> optionValues) {
        List<OptionValue> existingValues = option.getValues();

        for (OptionValue existingValue : existingValues) {
            if (!optionValues.contains(existingValue)) {
                valueRepository.delete(existingValue);
            }
        }

        for (OptionValue value : optionValues) {
            if (!existingValues.contains(value)) {
                value.setOption(option);
                valueRepository.save(value);
            }
        }
    }

    private void updateProductSkus(Product product, List<ProductSku> skus) {
        List<ProductSku> existingSkus = product.getSkus();

        for (ProductSku existingSku : existingSkus) {
            if (!skus.contains(existingSku)) {
                // Handle update or delete for existing SKU if necessary
                // ...
            }
        }

        for (ProductSku sku : skus) {
            if (!existingSkus.contains(sku)) {
                sku.setProduct(product);
                skuRepository.save(sku);
            }
        }
    }


    /**
     * Deactivate or activate a product based on its ID.
     *
     * @param productId ID of the product to deactivate or activate.
     * @return A ProductDTO representing the updated state of the product.
     * @throws ResourceNotFoundException if the product is not found.
     * @date 01-11-2023
     */
    @Override
    public ProductDTO toggleActiveStatus(Long productId) {
        Product existingProduct = findById(productId);

        existingProduct.setIsActive(!existingProduct.getIsActive());

        Product updateIsAcitve = productRepository.save(existingProduct);

        return mapToDTO(updateIsAcitve);
    }

    /**
     * Deactivate or activate the selling status of a product based on its ID.
     *
     * @param productId ID of the product to deactivate or activate selling.
     * @return A ProductDTO representing the updated status of the product.
     * @throws ResourceNotFoundException if the product is not found.
     * @date 01-11-2023
     */
    @Override
    public ProductDTO toggleSellingStatus(Long productId) {
        Product existingProduct = findById(productId);

        existingProduct.setIsSelling(!existingProduct.getIsSelling());

        Product updateIsAcitve = productRepository.save(existingProduct);

        return mapToDTO(updateIsAcitve);
    }

    @Override
    public void delete(Long id) {
        productRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(getMessage("product-not-found"))
        );
    }

    @Override
    public ProductDTO addQuantity(Long product_id, Integer quantity) {
        Product product = findById(product_id);
        if (quantity < 1) {
            throw new InvalidException(getMessage("the-additional-quantity-cannot-be-less-than-one"));
        }
        product.setQuantity(product.getQuantity() + quantity);
        product.setQuantityAvailable(product.getQuantityAvailable() + quantity);
        Product addQuantity = productRepository.save(product);
        return mapToDTO(addQuantity);
    }

    @Override
    public ProductResponse searchSortAndFilterProducts(Boolean sell, String key, List<String> cateNames, List<String> sortCriteria, int pageNo, int pageSize) {
        // follow Pageable instances
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

        // get all name of cate childs for this cate
        List<String> listCateNames = cateNames;
        if (cateNames != null && listCateNames.size() > 0) {
            listCateNames = getOnlyCateChild(cateNames);
        }

        Page<Product> productPage = productRepository.searchSortAndFilterProducts(
                sell, key, listCateNames, sortCriteria, pageable
        );

        // get content for page object
        List<Product> productList = productPage.getContent();

        List<ProductDTO> content = productList.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        // set data to the product response
        ProductResponse productResponse = new ProductResponse();
        long totalElements = productPage.getTotalElements();
        productResponse.setContent(content);
        productResponse.setPageNo(productPage.getNumber() + 1);
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setLast(productPage.isLast());

        long lastPageSize = totalElements % pageSize != 0 ?
                totalElements % pageSize : totalElements != 0 ?
                pageSize : 0;
        productResponse.setLastPageSize(lastPageSize);

        return productResponse;
    }

    private List<String> getOnlyCateChild(List<String> cateNames) {
        List<String> allCateNames = new ArrayList<>(cateNames);
        for (String cateName : cateNames) {
            Category category = categoryService.findByName(cateName);
            if (category.getChildren().size() > 0) {
                category.getChildren().forEach(child -> allCateNames.add(child.getName()));
            }
        }
        return allCateNames;
    }

    @Override
    public ProductResponse filter(Boolean sell, String searchTerm, List<String> cateNames, List<String> sortCriteria, int pageNo, int pageSize, String username) {
        ProductResponse response = searchSortAndFilterProducts(
                sell, searchTerm, cateNames, sortCriteria, pageNo, pageSize
        );
        if (username == null) {
            return response;
        }
        response.getContent().forEach((item) -> {
                    boolean isLiked = followRepository
                            .existsByProduct_ProductIdAndUser_UsernameAndIsDeletedFalse(
                                    item.getId(), username
                            );
                    item.setLiked(isLiked);
                }
        );
        return response;
    }

    @Transactional
    protected List<Option> saveOrUpdateOptions(Product existingProduct, List<OptionDTO> optionDTOList) {
        List<Option> options = optionDTOList.stream()
                .map(dto -> modelMapper.map(dto, Option.class))
                .collect(Collectors.toList());

        return optionService.saveOrUpdateOptions(existingProduct.getProductId(), options);
    }

    @Transactional
    protected List<ProductSku> saveOrUpdateSkus(Product existingProduct, List<ProductSkuDTO> skuDTOList) {
        List<ProductSku> skus = skuDTOList.stream()
                .map(skuDTO -> modelMapper.map(skuDTO, ProductSku.class))
                .collect(Collectors.toList());

        return productSkuService.saveOrUpdateSkus(existingProduct.getProductId(), skus);
    }

    /**
     * Build product object
     *
     * @param dto
     * @param existingProduct
     * @param category
     */
    private void setProductFields(ProductDTO dto, Product existingProduct, Category category) {
        if (!Objects.equals(existingProduct.getCategory().getId(), category.getId())) {
            existingProduct.setCategory(category);
        }

        // set fields
        existingProduct.setQuantity(dto.getQuantity());
        existingProduct.setPrice(dto.getPrice());
        existingProduct.setPromotionalPrice(dto.getPromotionalPrice());
        existingProduct.setDescription(dto.getDescription());

        // set unique slug for product
        if (!dto.getName().trim().equals(existingProduct.getName())) {
            existingProduct.setName(dto.getName());
            String uniqueSlug = slugGenerator.generateUniqueSlug(existingProduct, dto.getName());
            existingProduct.setSlug(uniqueSlug);
        }
    }

    public void setProductForChildEntity(Product product) {
        // Set this product for options
        for (Option option : product.getOptions()) {
            option.setProduct(product);
        }

        // Set this product for skus
        for (ProductSku sku : product.getSkus()) {
            sku.setProduct(product);
        }
    }

    private Product normalizeProduct(Product product) {
        return productValidator.normalizeInput(product);
    }

    private ProductDTO mapToDTO(Product product) {
        ProductDTO dto = modelMapper.map(product, ProductDTO.class);
        dto.setCategoryName(product.getCategory().getName());
        return dto;
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
