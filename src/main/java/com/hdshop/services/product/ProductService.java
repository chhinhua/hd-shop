package com.hdshop.services.product;

import com.hdshop.dtos.product.CreateProductDTO;
import com.hdshop.dtos.product.ProductDTO;
import com.hdshop.dtos.product.ProductResponse;

public interface ProductService {
    ProductDTO createProduct(final CreateProductDTO createProductDTO);

    ProductResponse getAllProducts(final int pageNo, final int pageSize);
}
