package com.hdshop.service.product.impl;

import com.hdshop.component.UniqueSlugGenerator;
import com.hdshop.dto.product.ProductDTO;
import com.hdshop.dto.product.ProductResponse;
import com.hdshop.dto.product.ProductSkuDTO;
import com.hdshop.entity.Category;
import com.hdshop.entity.product.Product;
import com.hdshop.exception.ResourceNotFoundException;
import com.hdshop.repository.CategoryRepository;
import com.hdshop.repository.product.ProductRepository;
import com.hdshop.service.product.ProductService;
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

        return mapToDTO(newProduct);
    }

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

    @Override
    public ProductDTO getOne(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        return mapToDTO(product);
    }

    /**
     * Update a product
     * @date 25-10-2023
     * @param product
     * @param productId
     * @return product DTO object
     */
    @Override
    public ProductDTO updateProduct(Product product, Long productId) {
        // check if product already exists
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product", "id", productId));

        // check if category already exists
        Category category = categoryRepository.findById(product.getCategory().getId())
                .orElseThrow(()-> new ResourceNotFoundException("Category", "id", product.getCategory().getId()));

        if (existingProduct.getCategory().getId() != (product.getCategory().getId())) {
            existingProduct.setCategory(category);
        }

        // set values
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setListImages(product.getListImages());
        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setQuantityAvailable(product.getQuantityAvailable());
        existingProduct.setPromotionalPrice(product.getPromotionalPrice());
        existingProduct.setOptions(product.getOptions());
        existingProduct.setSkus(product.getSkus());

        // set product cho mỗi sku
        existingProduct.getSkus().forEach(sku -> sku.setProduct(existingProduct));

        String uniqueSlug = slugGenerator.generateUniqueSlug(product, product.getName());
        existingProduct.setSlug(uniqueSlug);

        // save
        Product updateProduct = productRepository.save(existingProduct);

        return mapToDTO(updateProduct);
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
