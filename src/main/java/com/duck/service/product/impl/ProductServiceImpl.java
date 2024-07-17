package com.duck.service.product.impl;

import com.duck.component.UniqueSlugGenerator;
import com.duck.dto.product.*;
import com.duck.entity.*;
import com.duck.exception.BadCredentialsException;
import com.duck.exception.InvalidException;
import com.duck.exception.ResourceNotFoundException;
import com.duck.repository.ProductRepository;
import com.duck.service.category.CategoryService;
import com.duck.service.follow.FollowService;
import com.duck.service.product.OptionService;
import com.duck.service.product.ProductService;
import com.duck.service.product.ProductSkuService;
import com.duck.service.redis.RedisService;
import com.duck.service.user.UserService;
import com.duck.utils.AppUtils;
import com.duck.utils.enums.EProductAnalysisType;
import com.duck.utils.enums.EProductStatus;
import com.duck.validator.ProductValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.slugify.Slugify;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductServiceImpl implements ProductService {
    ProductRepository productRepository;
    ProductSkuService productSkuService;
    UserService userService;
    FollowService followService;
    OptionService optionService;
    CategoryService categoryService;
    UniqueSlugGenerator slugGenerator;
    ProductValidator productValidator;
    RedisService<Product> redisService;
    MessageSource messageSource;
    ModelMapper modelMapper;
    Slugify slugify;
    static Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Override
    @Transactional
    public void productAnalysis(Long productId, String analysisType) {
        EProductAnalysisType type = EProductAnalysisType.fromKey(analysisType);
        String fieldName = switch (type) {
            case CLICK -> "product_clicks";
            case VIEW -> "product_views";
            case ADD_CART -> "product_cart_adds";
        };
        productRepository.incrementField(productId, fieldName);
    }

    /**
     * 🎯Create a new product.
     *
     * @param product The {@link Product} object to follow.
     * @return {@link ProductDTO} representing the created product.
     * @throws ResourceNotFoundException if the corresponding category is not found.
     */
    @Override
    @Transactional
    public ProductDTO create(Product product) {
        productValidator.validateCreate(product);
        Category category = findCateByName(product.getCategory().getName());

        // build product
        int quantity = calculateProductQuantityCount(mapToDTO(product));
        BigDecimal price = calculateDiscountedPrice(product.getOriginalPrice(), product.getPercentDiscount());
        String uniqueSlug = slugGenerator.generateUniqueProductSlug(slugify.slugify(product.getName()));

        product.setSlug(uniqueSlug);
        product.setPrice(price);
        product.setQuantity(quantity);
        product.setQuantityAvailable(quantity);
        product.setSold(0);
        product.setRating(0f);
        product.setFavoriteCount(0);
        product.setProductCartAdds(0);
        product.setProductClicks(0);
        product.setProductViews(0);
        product.setNumberOfRatings(0);
        product.setPromotionalPrice(BigDecimal.ZERO);
        product.setStatus(EProductStatus.UNSELLING.getValue());
        product.setCategory(category);
        product.setIsSelling(false);
        product.setIsActive(true);
        setProductForChildEntity(product);

        Product normalizedProduct = normalizeProduct(product);  // normalize product information
        Product newProduct = productRepository.save(normalizedProduct);
        productSkuService.saveSkusProductCreation(newProduct);  // save information about product variants (productSkus)

        return mapToDTO(findById(newProduct.getProductId()));
    }

    /**
     * 🎯Get a single product.
     *
     * @param productId {@link Product} ID.
     * @return {@link ProductDTO} object.
     * @throws ResourceNotFoundException if the product is not found.
     */
    @Override
    public ProductDTO getOne(Long productId, Principal principal) {
        Product product = findById(productId);
        ProductDTO dto = mapToDTO(product);
        if (principal == null) {
            return dto;
        }
        boolean isLiked = followService.isFollowed(principal.getName(), dto.getId());
        dto.setLiked(isLiked);
        return dto;
    }

    @Override
    @Transactional
    public ProductDTO addInventory(AddInventoryRequest request) {
        request.getSkus().forEach(item -> {
            if (item.getAddNumber() < 0 || item.getAddNumber() > 10000) {
                throw new BadCredentialsException(getMessage("additional-quantity-must-be-from-1-to-10000"));
            }
            ProductSku sku = productSkuService.findById(item.getSkuId());
            sku.setQuantity(sku.getQuantity() + item.getAddNumber());
            sku.setQuantityAvailable(sku.getQuantityAvailable() + item.getAddNumber());
            productSkuService.save(sku);
        });

        int increaseProductQuantity = request.getSkus().stream()
                .mapToInt(AddInventoryRequest.SkuRequest::getAddNumber)
                .sum();
        Product product = findById(request.getProductId());
        product.setQuantity(product.getQuantity() + increaseProductQuantity);
        product.setQuantityAvailable(product.getQuantityAvailable() + increaseProductQuantity);

        return mapToDTO(productRepository.save(product));
    }

    @Override
    @Transactional
    public void makeDiscount(long productId, int percentDiscount) {
        if (percentDiscount < 0 || percentDiscount > 100) {
            throw new BadCredentialsException(getMessage("discount_percentage-must-have-a-value-between-0-and-100"));
        }

        Product product = findById(productId);
        product.setPercentDiscount(percentDiscount);
        product.setPrice(calculateDiscountedPrice(product.getOriginalPrice(), percentDiscount));
        productRepository.save(product);

        product.getSkus().forEach(sku -> {
            sku.setPercentDiscount(percentDiscount);
            sku.setPrice(calculateDiscountedPrice(sku.getOriginalPrice(), percentDiscount));
            productSkuService.save(sku);
        });
    }

    @Override
    public Product findById(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() ->
                new ResourceNotFoundException(getMessage("product-not-found"))
        );

        // Sắp xếp danh sách SKU theo valueName của Option có tên "size"
        List<ProductSku> skus = product.getSkus();
        skus.sort(Comparator.comparing(this::getValueNameForSizeOption));
        product.setSkus(skus);

        return product;
    }

    private String getValueNameForSizeOption(ProductSku sku) {
        return sku.getOptionValues().stream()
                .filter(optionValue -> optionValue.getOption().getOptionName().equalsIgnoreCase("size"))
                .findFirst()
                .map(OptionValue::getValueName)
                .orElse("");
    }

    /**
     * Updates an existing {@link Product} with new information.
     *
     * <ul>
     *      <li>1. Validates the input data</li>
     *      <li>2. Retrieves the existing {@link Product}</li>
     *      <li>3. Retrieves the existing {@link Category}</li>
     *      <li>4. Updates product fields</li>
     *      <li>5. Normalizes product data</li>
     *      <li>6. Saves the updated product</li>
     *      <li>7. Updates or creates {@link Option} and list {@link ProductSku}</li>
     * </ul>
     *
     * @param dto       The {@link ProductDTO} containing updated information
     * @param productId The ID of the product to update
     * @return A {@link ProductDTO} representing the updated product
     * @throws ResourceNotFoundException if the product or category is not found
     * @throws InvalidException          if the input data is invalid
     */
    @Override
    @Transactional
    public ProductDTO update(ProductDTO dto, Long productId) {
        productValidator.validateUpdate(dto);
        Product existingProduct = findById(productId);
        Category category = findCateByName(dto.getCategory().getName());
        setProductFields(dto, existingProduct, category);
        Product normalizedProduct = normalizeProduct(existingProduct);
        existingProduct = productRepository.save(normalizedProduct);
        saveOrUpdateOptions(existingProduct, dto.getOptions());
        saveOrUpdateSkus(existingProduct, dto.getSkus());
        return mapToDTO(findById(productId));
    }

    private Category findCateByName(String cateName) {
        return categoryService.findByName(cateName);
    }

    private void saveOrUpdateSkus(Product existingProduct, List<ProductSkuDTO> skuDTOList) {
        List<ProductSku> skus = skuDTOList.stream()
                .map(skuDTO -> modelMapper.map(skuDTO, ProductSku.class))
                .collect(Collectors.toList());
        productSkuService.saveOrUpdateListSkus(existingProduct.getProductId(), skus);
    }

    /**
     * 🎯Deactivate or activate a product based on its ID.
     *
     * @param productId ID of the {@link Product} to deactivate or activate.
     * @return A {@link ProductDTO} representing the updated state of the product.
     * @throws ResourceNotFoundException if the product is not found.
     * @date 01-11-2023
     */
    @Override
    public ProductDTO toggleActive(Long productId) {
        Product existingProduct = findById(productId);
        existingProduct.setIsActive(!existingProduct.getIsActive());
        existingProduct.setStatus(EProductStatus.getProductStatusValue(existingProduct));
        Product updateIsAcitve = productRepository.save(existingProduct);
        return mapToDTO(updateIsAcitve);
    }

    /**
     * 🎯
     * Deactivate or activate the selling status of a product based on its ID.
     *
     * @param productId ID of the {@link Product} to deactivate or activate selling.
     * @return A {@link ProductDTO} representing the updated status of the product.
     * @throws ResourceNotFoundException if the product is not found.
     * @date 01-11-2023
     */
    @Override
    public ProductDTO toggleSelling(Long productId) {
        Product existingProduct = findById(productId);
        existingProduct.setIsSelling(!existingProduct.getIsSelling());
        existingProduct.setStatus(EProductStatus.getProductStatusValue(existingProduct));
        Product updateIsAcitve = productRepository.save(existingProduct);
        return mapToDTO(updateIsAcitve);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        productRepository.deleteById(id);
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

    /**
     * Filters {@link Product} based on the provided criteria, retrieves them from a Redis cache if available, or
     * fetches from the database otherwise, and returns the results as a paginated response.
     *
     * @param sell         Boolean flag indicating whether to filter by sell status.
     * @param key          Keyword to filter the products.
     * @param cateNames    List of category names to filter the products.
     * @param sortCriteria List of sorting criteria to order the products.
     * @param pageNo       The page number to retrieve.
     * @param pageSize     The number of products per page.
     * @return {@link ProductResponse} A paginated response containing the filtered products.
     * @throws JsonProcessingException If there is an error processing JSON data.
     * @see <a href="https://redis.io/">More about Redis</a>
     */
    @Override
    public ProductResponse filter(Boolean sell, String key, List<String> cateNames, List<String> sortCriteria, int pageNo, int pageSize) throws JsonProcessingException {
        // get all name of cate childs for this cate
        List<String> listCateNames = (cateNames != null && !cateNames.isEmpty()) ? getOnlyCateChild(cateNames) : cateNames;
        logger.info(String.format("sell=%s, keyword=%s, cate_names=%s, sort=%s, page_no=%d, page_size=%d",
                sell, key, listCateNames, sortCriteria, pageNo, pageSize));

        String redisKey = redisService.getKeyFrom(AppUtils.KEY_PREFIX_GET_ALL_PRODUCT, sell, key, listCateNames, sortCriteria, pageNo, pageSize);
        ProductResponse response = redisService.getAll(redisKey, ProductResponse.class);
        if (response != null && !response.getContent().isEmpty()) {
            return response;
        }

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<Product> productPage = productRepository.filterProducts(sell, key, listCateNames, sortCriteria, pageable);

        // get content for page object
        List<ProductDTO> content = productPage.getContent().stream()
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
        productResponse.setLastPageSize(calculateLastPageSize(totalElements, pageSize));

        // save to redis cache
        redisService.saveAll(redisKey, productResponse);

        return productResponse;
    }

    /**
     * 🎯Tính kích thước của trang cuối cùng dựa trên tổng số phần tử và kích thước trang.
     *
     * <p>Nếu tổng số phần tử không chia hết cho kích thước trang,
     * trang cuối cùng sẽ chứa phần dư. Nếu không có phần tử nào,
     * trang cuối cùng sẽ có kích thước 0.</p>
     *
     * <p>Ví dụ:</p>
     * <ul>
     * <li>23 phần tử, trang 10 phần tử -> trang cuối có 3 phần tử.</li>
     * <li>20 phần tử, trang 10 phần tử -> trang cuối có 10 phần tử.</li>
     * <li>0 phần tử, trang 10 phần tử -> trang cuối có 0 phần tử.</li>
     * </ul>
     *
     * @param totalElements Tổng số phần tử.
     * @param pageSize      Kích thước của mỗi trang.
     * @return Số phần tử trong trang cuối cùng.
     */
    private long calculateLastPageSize(long totalElements, int pageSize) {
        return totalElements % pageSize != 0 ? totalElements % pageSize : totalElements != 0 ? pageSize : 0;
    }

    /**
     * Retrieves a list of category names, including the names of all child categories for each provided {@link Category} name.
     * <p>
     * This method trims whitespace from the input category names, removes duplicates, and processes each category name
     * to find its child categories. The names of child categories are then added to the result set.
     *
     * @param cateNames a list of category names to process
     * @return a list of unique category names, including child category names, with any encoded names decoded
     */
    private List<String> getOnlyCateChild(List<String> cateNames) {
        Set<String> allCateNames = cateNames.stream()
                .map(String::trim)
                .collect(Collectors.toSet());

        // Sử dụng Set để theo dõi các danh mục đã được xử lý
        Set<String> processedCategories = new HashSet<>();

        for (String cateName : allCateNames) {
            if (!processedCategories.contains(cateName)) {
                Category category = categoryService.findByName(cateName);
                if (category != null) {
                    category.getChildren().forEach(child -> allCateNames.add(child.getName()));
                }
                processedCategories.add(cateName);
            }
        }

        return allCateNames.stream()
                .map(AppUtils::decodeIfEncoded)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse filterProducts(Boolean sell, String searchTerm, List<String> cateNames, List<String> sortCriteria, int pageNo, int pageSize, String username) throws JsonProcessingException {
        ProductResponse response = filter(sell, searchTerm, cateNames, sortCriteria, pageNo, pageSize);
        if (username != null) {
            Set<Role> roles = userService.findByUsername(username).getRoles();
            if (roles.stream().anyMatch(role -> role.getName().equals(AppUtils.ROLE_ADMIN_NAME))) {
                return response; // Early return if admin
            }
            logger.info("filer product role user");
            retrieveUserFollowProduct(username, response.getContent());
        }
        return response;
    }

    @Override
    public void save(Product product) {
        productRepository.save(product);
    }

    private void retrieveUserFollowProduct(String username, List<ProductDTO> products) {
        products.forEach(item -> item.setLiked(followService.isFollowed(username, item.getId())));
    }

    private void saveOrUpdateOptions(Product existingProduct, List<OptionDTO> optionDTOList) {
        List<Option> options = optionDTOList.stream()
                .map(dto -> modelMapper.map(dto, Option.class))
                .collect(Collectors.toList());
        optionService.saveOrUpdateOptions(existingProduct.getProductId(), options);
    }

    /**
     * 🎯Build {@link Product} object
     *
     * @param dto is {@link ProductDTO}
     * @param existingProduct is {@link Product}
     * @param category is {@link Category}
     */
    private void setProductFields(ProductDTO dto, Product existingProduct, Category category) {
        if (!Objects.equals(existingProduct.getCategory().getId(), category.getId())) {
            existingProduct.setCategory(category);
        }

        int quantity = calculateProductQuantityCount(dto);
        BigDecimal price = calculateDiscountedPrice(dto.getOriginalPrice(), dto.getPercentDiscount());

        // set fields
        existingProduct.setOriginalPrice(dto.getOriginalPrice());
        existingProduct.setPercentDiscount(dto.getPercentDiscount());
        existingProduct.setQuantity(quantity);
        existingProduct.setPrice(price);
        existingProduct.setPromotionalPrice(dto.getPromotionalPrice());
        existingProduct.setDescription(dto.getDescription());

        // set unique slug for product
        if (!dto.getName().trim().equals(existingProduct.getName())) {
            existingProduct.setName(dto.getName());
            String uniqueSlug = slugGenerator.generateUniqueSlug(existingProduct, dto.getName());
            existingProduct.setSlug(uniqueSlug);
        }
    }

    private static int calculateProductQuantityCount(ProductDTO dto) {
        return dto.getSkus().stream().mapToInt(ProductSkuDTO::getQuantity).sum();
    }


    public static BigDecimal calculateDiscountedPrice(BigDecimal originalPrice, double percentDiscount) {
        BigDecimal discountRate = BigDecimal.valueOf(percentDiscount);
        BigDecimal hundred = new BigDecimal("100");
        BigDecimal discountAmount = originalPrice.multiply(discountRate)
                .divide(hundred, 2, RoundingMode.HALF_UP);
        BigDecimal discountedPrice = originalPrice.subtract(discountAmount);
        return discountedPrice.setScale(0, RoundingMode.DOWN);
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
