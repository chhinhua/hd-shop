package com.hdshop.service.product.impl;

import com.github.slugify.Slugify;
import com.hdshop.component.UniqueSlugGenerator;
import com.hdshop.dto.product.*;
import com.hdshop.entity.Category;
import com.hdshop.entity.product.*;
import com.hdshop.exception.APIException;
import com.hdshop.exception.ResourceNotFoundException;
import com.hdshop.repository.CategoryRepository;
import com.hdshop.repository.product.OptionRepository;
import com.hdshop.repository.product.ProductRepository;
import com.hdshop.service.product.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OptionService optionService;
    private final ProductSkuService productSkuService;
    private final OptionValueService optionValueService;
    private final ModelMapper modelMapper;
    private final Slugify slugify;
    private final UniqueSlugGenerator slugGenerator;
    private final OptionRepository optionRepository;

    @Override
    public ProductDTO createProduct(Product product) {
        Category category = categoryRepository.findById(product.getCategory().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", product.getCategory().getId()));

        String uniqueSlug = slugGenerator.generateUniqueSlug(product, product.getName());
        product.setSlug(uniqueSlug);
        product.setCategory(category);

        // Lưu thông tin sản phẩm
        Product newProduct = productRepository.save(product);

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
