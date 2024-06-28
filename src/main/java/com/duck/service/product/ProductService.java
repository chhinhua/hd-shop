package com.duck.service.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.duck.dto.product.ProductDTO;
import com.duck.dto.product.ProductResponse;
import com.duck.entity.Product;

import java.security.Principal;
import java.util.List;

public interface ProductService {
    Product findById(final Long productId);

    ProductDTO create(final Product product);

    ProductDTO update(final ProductDTO productDTO, final Long productId);

    ProductDTO toggleActive(final Long productId);

    ProductDTO toggleSelling(final Long productId);

    ProductDTO getOne(final Long productId, final Principal principal);

    // TODO update this implementation method
    ProductDTO addQuantity(final Long product_id, final Integer quantity);

    void delete(final Long id);

    void productAnalysis(final Long productId, final String analysisType);

    ProductResponse filter(
            Boolean sell,
            String searchTerm,
            List<String> cateName,
            List<String> sortCriteria,
            int pageNo,
            int pageSize
    ) throws JsonProcessingException;

    /**
     * Filter product for client has logedin account
     * @param sell
     * @param searchTerm
     * @param cateNames
     * @param sortCriteria
     * @param pageNo
     * @param pageSize
     * @param username
     * @return
     * @throws JsonProcessingException
     */
    ProductResponse filterProducts(
            Boolean sell,
            String searchTerm,
            List<String> cateNames,
            List<String> sortCriteria,
            int pageNo,
            int pageSize,
            String username
    ) throws JsonProcessingException;
}
