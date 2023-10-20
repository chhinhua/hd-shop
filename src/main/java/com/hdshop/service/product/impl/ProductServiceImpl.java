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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OptionService optionService;
    private final ProductSkuService productSkuService;
    private final OptionValueService optionValueService;
    private final SkuValueService skuValueService;
    private final ModelMapper modelMapper;
    private final Slugify slugify;
    private final UniqueSlugGenerator slugGenerator;
    private final OptionRepository optionRepository;

    /**
     * Create new product
     * @param dto (CreateProductDTO object)
     * @return ProductDTO object
     */
    @Override
    public ProductDTO createProduct(CreateProductDTO dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", dto.getCategoryId()));

        if (productRepository.existsProductByName(dto.getName())) {
            throw  new APIException(HttpStatus.BAD_REQUEST, "Product by name already exists");
        }

        Product product = modelMapper.map(dto, Product.class);

        String uniqueSlug = slugGenerator.generateUniqueSlug(product, product.getName());
        product.setSlug(uniqueSlug);
        product.setCategory(category);

        // Lưu thông tin sản phẩm
        Product newProduct = productRepository.save(product);

        // Thêm Options
        optionService.addOptions(product, product.getOptions());

        // Thêm Sku

        return mapToDTO(newProduct);
    }

    @Override
    public ProductDTO createProduct1(CreateProductDTO dto) {
        /*Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", dto.getCategoryId()));

        if (productRepository.existsProductByName(dto.getName())) {
            throw  new APIException(HttpStatus.BAD_REQUEST, "Product by name already exists");
        }

        Product product = modelMapper.map(dto, Product.class);

        String uniqueSlug = slugGenerator.generateUniqueSlug(product, product.getName());
        product.setSlug(uniqueSlug);
        product.setCategory(category);

        // Lưu thông tin sản phẩm
        Product newProduct = productRepository.save(product);

        // Lấy danh sách optionDTO từ productDTO chuyển hết thành Option entity
        List<Option> optionsFromDTO = dto.getOptions()
                .stream()
                .map((optionDTO) -> modelMapper.map(optionDTO, Option.class))
                .collect(Collectors.toList());

        // Lưu thông tin Options
        List<Option> options = optionService.addOptions(product, optionsFromDTO);
        product.setOptions(options);

        // Lưu thôgn tin OptionValues
        List<OptionValue> optionValues = optionValueService.addOptionValues(product, options, dto.getOptions());

        // Lấy danh sách OptionValueDTO từ productDTO chuyển hết thành OptionValue entity

        // Lấy danh sách SkuDTO từ productDTO chuyển hết thành ProductSku entity
        List<ProductSku> productSkuFromDTO = dto.getSkus()
                .stream()
                .map((skuDTO) -> modelMapper.map(skuDTO, ProductSku.class))
                .collect(Collectors.toList());

        // Lưu thông tin ProductSkus
        List<ProductSku> productSkus = productSkuService.addProductSkus(product, productSkuFromDTO);
        product.setProductSkus(productSkus);

        return mapToDTO(newProduct);*/

        return null;
    }

    @Override
    public ProductDTO createProduct2(CreateProductDTO createProductDTO) {
        Category category = categoryRepository.findById(createProductDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", createProductDTO.getCategoryId()));

        if (productRepository.existsProductByName(createProductDTO.getName())) {
            throw  new APIException(HttpStatus.BAD_REQUEST, "Product by name already exists");
        }

        Product product = modelMapper.map(createProductDTO, Product.class);

        String uniqueSlug = slugGenerator.generateUniqueSlug(product, product.getName());
        product.setSlug(uniqueSlug);
        product.setCategory(category);

        List<OptionDTO> optionsDTO = createProductDTO.getOptions();
        for (OptionDTO optionDTO : optionsDTO) {
            Option option = modelMapper.map(optionDTO, Option.class);
            option.setProduct(product);

            List<OptionValueDTO> valuesDTO = optionDTO.getValues();
            for (OptionValueDTO valueDTO : valuesDTO) {
                OptionValue optionValue = modelMapper.map(valueDTO, OptionValue.class);
                optionValue.setOption(option);

                option.getValues().add(optionValue);
            }

            List<ProductSku> productSkus = new ArrayList<>();
            for (ProductSkuDTO productSkuDTO : createProductDTO.getSkus()) {
                ProductSku productSku = modelMapper.map(productSkuDTO, ProductSku.class);
                productSku.setProduct(product);


                for (SkuValueDTO skuValueDTO : productSkuDTO.getSkuValues()) {
                    SkuValue skuValue = modelMapper.map(skuValueDTO, SkuValue.class);
                    skuValue.setProductSku(productSku);
                    skuValue.setOption(option);
                }

                productSkus.add(productSku);
            }

            product.getSkus().addAll(productSkus);
            product.getOptions().add(option);
        }

        // Lưu Product và các Entity liên quan bằng JpaRepository
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
