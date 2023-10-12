package com.hdshop.services.product;

import com.github.slugify.Slugify;
import com.hdshop.components.UniqueSlugGenerator;
import com.hdshop.dtos.product.CreateProductDTO;
import com.hdshop.dtos.product.ProductDTO;
import com.hdshop.dtos.product.ProductResponse;
import com.hdshop.entities.Category;
import com.hdshop.entities.Product;
import com.hdshop.exceptions.APIException;
import com.hdshop.exceptions.ResourceNotFoundException;
import com.hdshop.repositories.CategoryRepository;
import com.hdshop.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final Slugify slugify;
    private final UniqueSlugGenerator slugGenerator;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, ModelMapper modelMapper, Slugify slugify, UniqueSlugGenerator slugGenerator) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
        this.slugify = slugify;
        this.slugGenerator = slugGenerator;
    }

    /**
     * Create new product
     * @param createProductDTO (CreateProductDTO object)
     * @return ProductDTO object
     */
    @Override
    public ProductDTO createProduct(CreateProductDTO createProductDTO) {
        Category category = categoryRepository.findById(createProductDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", createProductDTO.getCategoryId()));

        if (productRepository.existsProductByName(createProductDTO.getName())) {
            throw  new APIException(HttpStatus.BAD_REQUEST, "Product by name already exists");
        }

        Product product = modelMapper.map(createProductDTO, Product.class);

        String uniqueSlug = slugGenerator.generateUniqueSlug(product, product.getName());
        product.setSlug(uniqueSlug);
        product.setCategory(category);

        Product newProduct = productRepository.save(product);

        return mapToDTO(newProduct);
    }

    @Override
    public ProductResponse getAllProducts(int pageNo, int pageSize) {
        // create Pageable instances
        Pageable pageable = PageRequest.of(pageNo-1, pageSize);

        Page<Product> productPage = productRepository.findAll(pageable);

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
