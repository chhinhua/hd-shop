package com.duck.service.product.impl;

import com.duck.component.UniqueSlugGenerator;
import com.duck.dto.product.OptionDTO;
import com.duck.dto.product.ProductDTO;
import com.duck.dto.product.ProductResponse;
import com.duck.dto.product.ProductSkuDTO;
import com.duck.entity.*;
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
    public void productAnalysis(Long productId, String analysisType) {
        Product product = findById(productId);
        Integer clicks = product.getProductClicks();
        Integer views = product.getProductViews();
        Integer cart_adds = product.getProductCartAdds();
        switch (analysisType.trim()) {
            case "click" -> product.setProductClicks(clicks != null ? clicks + 1 : 1);
            case "view" -> product.setProductViews(views != null ? views + 1 : 1);
            case "add_cart" -> product.setProductCartAdds(cart_adds != null ? cart_adds + 1 : 1);
        }
        productRepository.save(product);
    }

    /**
     * 🎯Create a new product.
     *
     * @param product The product object to follow.
     * @return ProductDTO representing the created product.
     * @throws ResourceNotFoundException if the corresponding category is not found.
     */
    @Override
    @Transactional
    public ProductDTO create(Product product) {
        productValidator.validate(product);  // validate input product
        Category category = categoryService.findByName(product.getCategory().getName());    // find the product category based on its ID

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
        product.setPromotionalPrice(BigDecimal.ZERO);
        product.setQuantityAvailable(product.getQuantity());
        setProductForChildEntity(product);

        Product normalizedProduct = normalizeProduct(product);  // normalize product information
        Product newProduct = productRepository.save(normalizedProduct); // save the product to the database
        productSkuService.saveSkusFromProduct(newProduct);  // save information about product variants (productSkus)

        return mapToDTO(findById(newProduct.getProductId()));   // convert the product to a ProductDTO object and return it
    }

    /**
     * 🎯Get a single product.
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
        boolean isLiked = followService.isFollowed(principal.getName(), dto.getId());
        dto.setLiked(isLiked);
        return dto;
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
     * 🎯Update a product.
     *
     * @param dto       Updated product information.
     * @param productId Product ID to update.
     * @return Updated product DTO object.
     * @date 25-10-2023
     */
    @Override
    @Transactional
    public ProductDTO update(ProductDTO dto, Long productId) {
        productValidator.validateUpdate(dto);  // xác thực đầu vào
        Product existingProduct = findById(productId);  // Kiểm tra sản phẩm đã tồn tại
        Category category = categoryService.findByName(dto.getCategory().getName()); // Kiểm tra danh mục đã tồn tại
        setProductFields(dto, existingProduct, category);    // Cập nhật các trường thay đổi
        Product normalizedProduct = normalizeProduct(existingProduct);  // Chuẩn hóa dữ liệu sản phẩm đầu vào
        existingProduct = productRepository.save(normalizedProduct);    // Lưu sản phẩm đã cập nhật
        saveOrUpdateOptions(existingProduct, dto.getOptions()); // Lưu hoặc cập nhật Options và OptionValues
        saveOrUpdateSkus(existingProduct, dto.getSkus());  // Lưu hoặc cập nhật ProductSkus
        return mapToDTO(findById(productId));
    }

    /**
     * 🎯Deactivate or activate a product based on its ID.
     *
     * @param productId ID of the product to deactivate or activate.
     * @return A ProductDTO representing the updated state of the product.
     * @throws ResourceNotFoundException if the product is not found.
     * @date 01-11-2023
     */
    @Override
    public ProductDTO toggleActive(Long productId) {
        Product existingProduct = findById(productId);
        existingProduct.setIsActive(!existingProduct.getIsActive());
        Product updateIsAcitve = productRepository.save(existingProduct);
        return mapToDTO(updateIsAcitve);
    }

    /**
     * 🎯Deactivate or activate the selling status of a product based on its ID.
     *
     * @param productId ID of the product to deactivate or activate selling.
     * @return A ProductDTO representing the updated status of the product.
     * @throws ResourceNotFoundException if the product is not found.
     * @date 01-11-2023
     */
    @Override
    public ProductDTO toggleSelling(Long productId) {
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

    private List<String> getOnlyCateChild(List<String> cateNames) {
        // Sử dụng Set để tránh các mục trùng lặp
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

    private void retrieveUserFollowProduct(String username, List<ProductDTO> products) {
        products.forEach(item -> item.setLiked(followService.isFollowed(username, item.getId())));
    }

    private void saveOrUpdateOptions(Product existingProduct, List<OptionDTO> optionDTOList) {
        List<Option> options = optionDTOList.stream()
                .map(dto -> modelMapper.map(dto, Option.class))
                .collect(Collectors.toList());
        optionService.saveOrUpdateOptions(existingProduct.getProductId(), options);
    }

    private void saveOrUpdateSkus(Product existingProduct, List<ProductSkuDTO> skuDTOList) {
        List<ProductSku> skus = skuDTOList.stream()
                .map(skuDTO -> modelMapper.map(skuDTO, ProductSku.class))
                .collect(Collectors.toList());
        productSkuService.saveOrUpdateSkus(existingProduct.getProductId(), skus);
    }

    /**
     * 🎯Build product object
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
        existingProduct.setOriginalPrice(dto.getOriginalPrice());
        existingProduct.setPercentDiscount(dto.getPercentDiscount());
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
