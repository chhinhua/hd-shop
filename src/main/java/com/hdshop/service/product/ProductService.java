package com.hdshop.service.product;

import com.hdshop.dto.product.ProductDTO;
import com.hdshop.dto.product.ProductResponse;
import com.hdshop.entity.Product;

public interface ProductService {
    ProductDTO createProduct(final Product product);

    ProductResponse getAllProducts(final int pageNo, final int pageSize);

    ProductDTO getOne(final Long productId);

    ProductDTO updateProduct(final ProductDTO productDTO, final Long productId);

    ProductDTO toggleProductActiveStatus(final Long productId);

    ProductDTO toggleProductSellingStatus(final Long productId);

}
