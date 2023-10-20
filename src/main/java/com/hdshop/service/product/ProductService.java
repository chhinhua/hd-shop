package com.hdshop.service.product;

import com.hdshop.dto.product.CreateProductDTO;
import com.hdshop.dto.product.ProductDTO;
import com.hdshop.dto.product.ProductResponse;
import com.hdshop.entity.product.Product;

public interface ProductService {
    ProductDTO createProduct(final CreateProductDTO dto);

    ProductDTO createProduct1(final CreateProductDTO dto);

    ProductDTO createProduct2(final CreateProductDTO dto);

    ProductResponse getAllProducts(final int pageNo, final int pageSize);
}
