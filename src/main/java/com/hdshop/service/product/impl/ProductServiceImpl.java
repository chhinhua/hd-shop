package com.hdshop.service.product.impl;

import com.github.slugify.Slugify;
import com.hdshop.component.UniqueSlugGenerator;
import com.hdshop.dto.product.OptionDTO;
import com.hdshop.dto.product.ProductDTO;
import com.hdshop.dto.product.ProductResponse;
import com.hdshop.dto.product.ProductSkuDTO;
import com.hdshop.entity.Category;
import com.hdshop.entity.Option;
import com.hdshop.entity.Product;
import com.hdshop.entity.ProductSku;
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
    private final CategoryRepository categoryRepository;
    private final FollowRepository followRepository;
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
        // find the product category based on its ID
        Category category = categoryRepository.findByName(product.getCategory().getName())
                .orElseThrow(() -> new ResourceNotFoundException(getMessage("category-not-found")));

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
        return mapToDTO(newProduct);
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
        Product product = getExistingProductById(productId);
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
        return getExistingProductById(productId);
    }

    /**
     * Update a product.
     *
     * @param productDTO Updated product information.
     * @param productId  Product ID to update.
     * @return Updated product DTO object.
     * @date 25-10-2023
     */
    @Override
    @Transactional
    public ProductDTO update(ProductDTO productDTO, Long productId) {
        // check if product already exists
        Product existingProduct = getExistingProductById(productId);

        // check if category already exists
        Category category = categoryRepository.findByName(productDTO.getCategory().getName())
                .orElseThrow(() -> new ResourceNotFoundException(getMessage("category-not-found")));

        // set changes fields
        setProductFields(productDTO, existingProduct, category);

        // normalize product input
        Product normalizedProduct = normalizeProduct(existingProduct);

        // save product
        existingProduct = productRepository.save(normalizedProduct);

        // save or update options & optionValues
        List<Option> options = saveOrUpdateOptions(existingProduct, productDTO.getOptions());
        existingProduct.setOptions(options);

        // save or update productSkus
        List<ProductSku> skus = saveOrUpdateSkus(existingProduct, productDTO.getSkus());
        existingProduct.setSkus(skus);

        return mapToDTO(existingProduct);
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
        Product existingProduct = getExistingProductById(productId);

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
        Product existingProduct = getExistingProductById(productId);

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
        List<Option> optionListFromDTO = optionDTOList.stream()
                .map(optionDTO -> modelMapper.map(optionDTO, Option.class))
                .collect(Collectors.toList());

        return optionService.saveOrUpdateOptionsByProductId(existingProduct.getProductId(), optionListFromDTO);
    }

    /**
     * Set fields for product entity is values from productDTO and Category entity
     *
     * @param productDTO
     * @param existingProduct
     * @param category
     */
    private void setProductFields(ProductDTO productDTO, Product existingProduct, Category category) {
        if (!Objects.equals(existingProduct.getCategory().getId(), productDTO.getCategoryId())) {
            existingProduct.setCategory(category);
        }

        // set fields
        existingProduct.setName(productDTO.getName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setListImages(productDTO.getListImages());
        existingProduct.setQuantity(productDTO.getQuantity());
        existingProduct.setQuantityAvailable(productDTO.getQuantityAvailable());
        existingProduct.setPromotionalPrice(productDTO.getPromotionalPrice());

        // set unique slug for product
        String uniqueSlug = slugGenerator.generateUniqueSlug(existingProduct, productDTO.getName());
        existingProduct.setSlug(uniqueSlug);
    }

    protected List<ProductSku> saveOrUpdateSkus(Product existingProduct, List<ProductSkuDTO> skuDTOList) {
        List<ProductSku> skuListFromDTO = skuDTOList.stream()
                .map(skuDTO -> modelMapper.map(skuDTO, ProductSku.class))
                .collect(Collectors.toList());

        return productSkuService.saveOrUpdateSkus(existingProduct.getProductId(), skuListFromDTO);
    }

    private Product getExistingProductById(Long productId) {
        return productRepository
                .findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(getMessage("product-not-found")));
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

    private boolean checkLiked(Long productId, String username) {
        boolean liked = false;
        if (username != null) {
            liked = followRepository.existsByProduct_ProductIdAndUser_UsernameAndIsDeletedFalse(productId, username);
        }
        return liked;
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
