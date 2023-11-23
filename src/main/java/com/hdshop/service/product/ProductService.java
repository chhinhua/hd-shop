package com.hdshop.service.product;

import com.hdshop.dto.product.ProductDTO;
import com.hdshop.dto.product.ProductResponse;
import com.hdshop.entity.Product;

import java.util.List;

public interface ProductService {
    ProductDTO create(final Product product);

    ProductResponse getAllIsActive(final int pageNo, final int pageSize);

    ProductDTO getOne(final Long productId);

    Product findById(final Long productId);

    ProductDTO update(final ProductDTO productDTO, final Long productId);

    ProductDTO toggleActiveStatus(final Long productId);

    ProductDTO toggleSellingStatus(final Long productId);

    ProductResponse searchSortAndFilterProducts(
            String searchTerm,
            List<String> cateName,
            List<String> sortCriteria,
            int pageNo,
            int pageSize
    );
}
