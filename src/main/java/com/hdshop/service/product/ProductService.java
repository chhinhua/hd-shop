package com.hdshop.service.product;

import com.hdshop.dto.product.CreateProductDTO;
import com.hdshop.dto.product.ProductDTO;
import com.hdshop.dto.product.ProductResponse;

public interface ProductService {
    ProductDTO createProduct(final CreateProductDTO createProductDTO);

    ProductResponse getAllProducts(final int pageNo, final int pageSize);
}
