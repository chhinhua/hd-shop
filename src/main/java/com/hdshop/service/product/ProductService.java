package com.hdshop.service.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hdshop.dto.product.ProductDTO;
import com.hdshop.dto.product.ProductResponse;
import com.hdshop.entity.Product;

import java.security.Principal;
import java.util.List;

public interface ProductService {
    void productAnalysis(final Long productId, final String analysisType);

    ProductDTO create(final Product product);

    ProductResponse getAllIsActive(final int pageNo, final int pageSize);

    ProductDTO getOne(final Long productId, final Principal principal);

    Product findById(final Long productId);

    ProductDTO update(final ProductDTO productDTO, final Long productId);

    ProductDTO toggleActive(final Long productId);

    ProductDTO toggleSelling(final Long productId);

    void delete(final Long id);

    ProductDTO addQuantity(final Long product_id, final Integer quantity);

    ProductResponse filter(
            Boolean sell,
            String searchTerm,
            List<String> cateName,
            List<String> sortCriteria,
            int pageNo,
            int pageSize
    ) throws JsonProcessingException;

    ProductResponse filterForUser(
            Boolean sell,
            String searchTerm,
            List<String> cateNames,
            List<String> sortCriteria,
            int pageNo,
            int pageSize,
            String username) throws JsonProcessingException;
}
