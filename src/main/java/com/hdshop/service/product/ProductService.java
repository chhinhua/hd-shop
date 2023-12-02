package com.hdshop.service.product;

import com.hdshop.dto.product.ProductDTO;
import com.hdshop.dto.product.ProductResponse;
import com.hdshop.entity.Product;

import java.security.Principal;
import java.util.List;

public interface ProductService {
    ProductDTO create(final Product product);

    ProductResponse getAllIsActive(final int pageNo, final int pageSize);

    ProductDTO getOne(final Long productId, final Principal principal);

    Product findById(final Long productId);

    ProductDTO update(final ProductDTO productDTO, final Long productId);

    ProductDTO update2(final Product product, final Long productId);

    ProductDTO toggleActiveStatus(final Long productId);

    ProductDTO toggleSellingStatus(final Long productId);

    void delete(final Long id);

    ProductDTO addQuantity(final Long product_id, final Integer quantity);

    ProductResponse searchSortAndFilterProducts(
            Boolean sell,
            String searchTerm,
            List<String> cateName,
            List<String> sortCriteria,
            int pageNo,
            int pageSize
    );

    ProductResponse filter(
            Boolean sell,
            String searchTerm,
            List<String> cateName,
            List<String> sortCriteria,
            int pageNo,
            int pageSize,
            String username);
}
