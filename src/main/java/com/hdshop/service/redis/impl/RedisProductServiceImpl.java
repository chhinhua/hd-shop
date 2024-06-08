package com.hdshop.service.redis.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hdshop.dto.product.ProductResponse;
import com.hdshop.service.product.impl.ProductServiceImpl;
import com.hdshop.service.redis.RedisProductService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RedisProductServiceImpl implements RedisProductService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper redisObjectMapper;
    @Value("${spring.data.redis.use-redis-cache}")
    private boolean useRedisCache;
    private static Logger logger = LoggerFactory.getLogger(RedisProductServiceImpl.class);

    private String getKeyFrom(String searchTerm, List<String> cateNames, List<String> sortCriteria, int pageNo, int pageSize) {
        String key = String.format("all_products:%s:%s:%s:%d:%d", searchTerm, cateNames, sortCriteria, pageNo, pageSize);
        return key;
    }

    @Override
    public void clear() {
        Cursor<byte[]> cursor = redisTemplate
                .getConnectionFactory()
                .getConnection()
                .scan(ScanOptions.scanOptions().match("all_products*").build());
        while (cursor.hasNext()) {
            String key = Arrays.toString(cursor.next());
            if (key.startsWith("all_products")) {
                redisTemplate.delete(key);
                logger.info("deleted cache data for key: " + key);
            }
        }
        cursor.close(); // Close the cursor to release resources
    }

    @Override
    public ProductResponse getAllProducts(String searchTerm, List<String> cateNames, List<String> sortCriteria, int pageNo, int pageSize) throws JsonProcessingException {
        if (!useRedisCache) {
            return null;
        }
        String key = this.getKeyFrom(searchTerm, cateNames, sortCriteria, pageNo, pageSize);
        String json = (String) redisTemplate.opsForValue().get(key);
        ProductResponse response = json != null ?
                redisObjectMapper.readValue(json, new TypeReference<>() {
                })
                : null;
        return response;
    }

    @Override
    public void saveAllProducts(ProductResponse productResponse, String searchTerm, List<String> cateNames, List<String> sortCriteria, int pageNo, int pageSize) throws JsonProcessingException {
        String key = this.getKeyFrom(searchTerm, cateNames, sortCriteria, pageNo, pageSize);
        String json = redisObjectMapper.writeValueAsString(productResponse);
        assert json != null;
        redisTemplate.opsForValue().set(key, json);
    }
}
