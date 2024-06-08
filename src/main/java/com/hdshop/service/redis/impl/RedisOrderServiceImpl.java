package com.hdshop.service.redis.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hdshop.dto.order.OrderPageResponse;
import com.hdshop.dto.order.OrderResponse;
import com.hdshop.dto.product.ProductResponse;
import com.hdshop.service.redis.RedisOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisOrderServiceImpl implements RedisOrderService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper redisObjectMapper;
    @Value("${spring.data.redis.use-redis-cache}")
    private boolean useRedisCache;

    private String getKeyFrom(String username, String statusValue, String keySearch, int pageNo, int pageSize) {
        String key = String.format("my_orders:%s:%s:%s:%d:%d",username, statusValue, keySearch, pageNo, pageSize);
        return key;
    }

    @Override
    public void clear() {

    }

    @Override
    public OrderPageResponse getMyOrders(String statusValue, String keySearch, int pageNo, int pageSize, String username) throws JsonProcessingException {
        if (!useRedisCache) {
            return null;
        }
        String key = this.getKeyFrom(username, statusValue, keySearch, pageNo, pageSize);
        String json = (String) redisTemplate.opsForValue().get(key);
        OrderPageResponse response = json != null ?
                redisObjectMapper.readValue(json, new TypeReference<>() {
                }) : null;
        return response;
    }

    @Override
    public void saveMyOrders(OrderPageResponse response, String statusValue, String keySearch, int pageNo, int pageSize, String username) throws JsonProcessingException {
        String key = this.getKeyFrom(username, statusValue, keySearch, pageNo, pageSize);
        String json = redisObjectMapper.writeValueAsString(response);
        assert json != null;
        redisTemplate.opsForValue().set(key, json);
    }


}
