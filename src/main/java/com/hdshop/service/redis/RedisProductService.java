package com.hdshop.service.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hdshop.dto.product.ProductDTO;
import com.hdshop.dto.product.ProductResponse;

import java.util.List;

public interface RedisProductService {
    void clear();

    ProductResponse getAllProducts(String searchTerm,
                                   List<String> cateNames,
                                   List<String> sortCriteria,
                                   int pageNo,
                                   int pageSize) throws JsonProcessingException;

    void saveAllProducts(ProductResponse productResponse,
                         String searchTerm,
                         List<String> cateNames,
                         List<String> sortCriteria,
                         int pageNo,
                         int pageSize) throws JsonProcessingException;
}
