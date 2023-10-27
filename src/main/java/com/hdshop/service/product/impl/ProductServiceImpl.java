package com.hdshop.service.product.impl;

import com.hdshop.component.UniqueSlugGenerator;
import com.hdshop.dto.product.ProductDTO;
import com.hdshop.dto.product.ProductResponse;
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

    /**
     * Create new product
     * @param product
     * @return productDTO
     */
    @Override
    public ProductDTO createProduct(Product product) {
        Category category = categoryRepository.findById(product.getCategory().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", product.getCategory().getId()));

        String uniqueSlug = slugGenerator.generateUniqueSlug(product, product.getName());
        product.setSlug(uniqueSlug);
        product.setCategory(category);

        // Validate
        Product productValid = productValidator.normalizeInput(product);

        // Lưu thông tin sản phẩm
        Product newProduct = productRepository.save(productValid);

        // Lưu productSku
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

        if (existingProduct.getCategory().getId() != (productDTO.getCategoryId())) {
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

        // save
        existingProduct = productRepository.save(existingProduct);

        // Convert list optionDTO to list option entity
        List<Option> optionListFromDTO = productDTO.getOptions().stream()
                .map((optionDTO) -> modelMapper.map(optionDTO, Option.class))
                .collect(Collectors.toList());

        // Save or update options
        List<Option> options = optionService
                .addOptionsByProductId(existingProduct.getProductId(), optionListFromDTO);
        existingProduct.setOptions(options);

        // Convert list optionDTO to list option entity
        List<ProductSku> skuListFromDTO = productDTO.getSkus().stream()
                .map((skuDTO) -> modelMapper.map(skuDTO, ProductSku.class))
                .collect(Collectors.toList());

        // save or update ProductSkus
        // TODO  cập nhật product còn phần skus
        List<ProductSku> skus = productSkuService
                .saveOrUpdateSkus(existingProduct.getProductId(), skuListFromDTO);
        existingProduct.setSkus(skus);

        return mapToDTO(existingProduct);
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

    /**
     * Convert Product entity to ProductDTO
     *
     * @param product
     * @return ProductDTO object
     */
    private ProductDTO mapToDTO(Product product) {
        return modelMapper.map(product, ProductDTO.class);
    }
}
