package com.hdshop.service.product.impl;

import com.github.slugify.Slugify;
import com.hdshop.component.UniqueSlugGenerator;
import com.hdshop.dto.product.OptionDTO;
import com.hdshop.dto.product.ProductDTO;
import com.hdshop.dto.product.ProductResponse;
import com.hdshop.dto.product.ProductSkuDTO;
import com.hdshop.entity.Category;
import com.hdshop.entity.product.Option;
import com.hdshop.entity.product.Product;
import com.hdshop.entity.product.ProductSku;
import com.hdshop.exception.ResourceNotFoundException;
import com.hdshop.repository.CategoryRepository;
import com.hdshop.repository.product.ProductRepository;
import com.hdshop.service.product.OptionService;
import com.hdshop.service.product.ProductService;
import com.hdshop.service.product.ProductSkuService;
import com.hdshop.validator.ProductValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final UniqueSlugGenerator slugGenerator;
    private final ProductValidator productValidator;
    private final ProductSkuService productSkuService;
    private final OptionService optionService;
    private final Slugify slugify;

    /**
     * Create new product
     * @param product
     * @return productDTO
     */
    @Override
    public ProductDTO createProduct(Product product) {
        Category category = categoryRepository.findById(product.getCategory().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", product.getCategory().getId()));

        String uniqueSlug = slugGenerator.generateUniqueProductSlug(slugify.slugify(product.getName()));
        product.setSlug(uniqueSlug);
        product.setCategory(category);

        // normalize product input
        Product normalizedProduct = normalizeProduct(product);

        // save product
        Product newProduct = productRepository.save(normalizedProduct);

        // save productSkus
        productSkuService.saveSkusFromProduct(newProduct);

        return mapToDTO(newProduct);
    }

    /**
     * Get all product pagination
     * @param pageNo
     * @param pageSize
     * @return list of product pagination
     */
    @Override
    public ProductResponse getAllProducts(int pageNo, int pageSize) {
        // create Pageable instances
        Pageable pageable = PageRequest.of(pageNo-1, pageSize);

        Page<Product> productPage = productRepository.findAllByIsActiveIsTrue(pageable);

        // get content for page object
        List<Product> productList = productPage.getContent();

        List<ProductDTO> content = productList.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        // set data to the product response
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(content);
        productResponse.setPageNo(productPage.getNumber() + 1);
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setLast(productPage.isLast());

        return productResponse;
    }

    /**
     * Get the single product
     * @param productId
     * @return product DTO object
     */
    @Override
    public ProductDTO getOne(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        return mapToDTO(product);
    }

    /**
     * Update a product
     * @date 25-10-2023
     * @param productDTO
     * @param productId
     * @return product DTO object
     */
    @Override
    public ProductDTO updateProduct(ProductDTO productDTO, Long productId) {
        // check if product already exists
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product", "id", productId));
        
        // check if category already exists
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(()-> new ResourceNotFoundException("Category", "id", productDTO.getCategoryId()));

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

    private Product normalizeProduct(Product product) {
        return productValidator.normalizeInput(product);
    }

    /**
     * Set fields for product entity is values from productDTO and Category entity
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

    /**
     * Save or update options if it exists
     * @param existingProduct
     * @param optionDTOList
     * @return option entity list
     */
    private List<Option> saveOrUpdateOptions(Product existingProduct, List<OptionDTO> optionDTOList) {
        List<Option> optionListFromDTO = optionDTOList.stream()
                .map(optionDTO -> modelMapper.map(optionDTO, Option.class))
                .collect(Collectors.toList());

        return optionService.saveOrUpdateOptionsByProductId(existingProduct.getProductId(), optionListFromDTO);
    }

    /**
     * Save or update productSkus if it exists
     * @param existingProduct
     * @param skuDTOList
     * @return productSku entity list
     */
    private List<ProductSku> saveOrUpdateSkus(Product existingProduct, List<ProductSkuDTO> skuDTOList) {
        List<ProductSku> skuListFromDTO = skuDTOList.stream()
                .map(skuDTO -> modelMapper.map(skuDTO, ProductSku.class))
                .collect(Collectors.toList());

        return productSkuService.saveOrUpdateSkus(existingProduct.getProductId(), skuListFromDTO);
    }

    /**
     * Convert ProductDTO to  Product entity class
     *
     * @param productDTO
     * @return Product entity object
     */
    private Product mapToEntity(ProductDTO productDTO) {
        return modelMapper.map(productDTO, Product.class);
    }

    private ProductDTO mapToDTO(Product product) {
        return modelMapper.map(product, ProductDTO.class);
    }
}
