package com.hdshop.service.product.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import com.hdshop.service.follow.FollowService;
import com.hdshop.service.product.OptionService;
import com.hdshop.service.product.ProductService;
import com.hdshop.service.product.ProductSkuService;
import com.hdshop.service.redis.RedisProductService;
import com.hdshop.utils.AppUtils;
import com.hdshop.validator.ProductValidator;
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
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductServiceImpl implements ProductService {
    ProductRepository productRepository;
    CategoryRepository categoryRepository;
    RedisProductService redisProductService;
    ProductSkuService productSkuService;
    FollowService followService;
    CategoryService categoryService;
    OptionService optionService;
    UniqueSlugGenerator slugGenerator;
    ProductValidator productValidator;
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
     * Create a new product.
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
     * Update a product.
     *
     * @param dto       Updated product information.
     * @param productId Product ID to update.
     * @return Updated product DTO object.
     * @date 25-10-2023
     */
    @Override
    @Transactional
    public ProductDTO update(ProductDTO dto, Long productId) {
        productValidator.validateUpdate(dto);  // validate product update data
        Product existingProduct = findById(productId);  // Kiểm tra nếu sản phẩm đã tồn tại
        Category category = categoryService.findByName(dto.getCategory().getName()); // Kiểm tra nếu danh mục đã tồn tại
        setProductFields(dto, existingProduct, category);    // Cập nhật các trường thay đổi
        Product normalizedProduct = normalizeProduct(existingProduct);  // Chuẩn hóa dữ liệu sản phẩm đầu vào
        existingProduct = productRepository.save(normalizedProduct);    // Lưu sản phẩm đã cập nhật
        saveOrUpdateOptions(existingProduct, dto.getOptions()); // Lưu hoặc cập nhật Options và OptionValues
        saveOrUpdateSkus(existingProduct, dto.getSkus());  // Lưu hoặc cập nhật ProductSkus
        redisProductService.clear(); // Xóa bộ nhớ đệm của Product
        return mapToDTO(findById(productId));
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
    public ProductDTO toggleActive(Long productId) {
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
        List<String> listCateNames = cateNames;
        if (cateNames != null && listCateNames.size() > 0) {
            listCateNames = getOnlyCateChild(cateNames);
        }
        logger.info(String.format("keyword = %s, cate_names = %s, sort = %s, page_no = %d, page_size = %d",
                key, cateNames, sortCriteria, pageNo, pageSize));

        ProductResponse response = redisProductService.getAllProducts(key, listCateNames, sortCriteria, pageNo, pageSize);
        if (response != null && !response.getContent().isEmpty()) {
            return response;
        }

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<Product> productPage = productRepository.filterProducts(
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

        // save to redis cache
        redisProductService.saveAllProducts(
                productResponse,
                key,
                cateNames,
                sortCriteria,
                pageNo,
                pageSize
        );

        return productResponse;
    }

    private List<String> getOnlyCateChild(List<String> cateNames) {
        List<String> allCateNames = trims(cateNames);

        for (String cateName : cateNames) {
            Optional<Category> category = categoryRepository.findByName(cateName);
            if (category.isPresent()) {
                if (category.get().getChildren().size() > 0) {
                    category.get().getChildren().forEach(child -> allCateNames.add(child.getName()));
                }
            }
        }
        return decodeUrl(allCateNames);
    }

    private List<String> decodeUrl(List<String> cateNames) {
        return cateNames.stream().map(AppUtils::decodeIfEncoded).collect(Collectors.toList());
    }

    private List<String> trims(List<String> strings) {
        return strings.stream().map(String::trim).collect(Collectors.toList());
    }

    @Override
    public ProductResponse filterForUser(Boolean sell, String searchTerm, List<String> cateNames, List<String> sortCriteria, int pageNo, int pageSize, String username) throws JsonProcessingException {
        ProductResponse response = filter(
                sell,
                searchTerm,
                cateNames,
                sortCriteria,
                pageNo,
                pageSize
        );
        if (username == null) {
            return response;
        }
        response.getContent().forEach((item) -> {
                    boolean isLiked = followService.isFollowed(username, item.getId());
                    item.setLiked(isLiked);
                }
        );
        return response;
    }


    @Transactional
    protected void saveOrUpdateOptions(Product existingProduct, List<OptionDTO> optionDTOList) {
        List<Option> options = optionDTOList.stream()
                .map(dto -> modelMapper.map(dto, Option.class))
                .collect(Collectors.toList());
        optionService.saveOrUpdateOptions(existingProduct.getProductId(), options);
    }

    @Transactional
    protected void saveOrUpdateSkus(Product existingProduct, List<ProductSkuDTO> skuDTOList) {
        List<ProductSku> skus = skuDTOList.stream()
                .map(skuDTO -> modelMapper.map(skuDTO, ProductSku.class))
                .collect(Collectors.toList());
        productSkuService.saveOrUpdateSkus(existingProduct.getProductId(), skus);
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
