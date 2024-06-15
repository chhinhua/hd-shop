package com.duck.service.redis;

import com.duck.entity.Category;
import com.duck.entity.Order;
import com.duck.entity.Product;
import com.duck.entity.ProductSku;
import com.duck.utils.AppUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl<T> implements RedisService<T> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper redisObjectMapper;
    private static final Logger logger = LoggerFactory.getLogger(RedisServiceImpl.class);
    @Value("${spring.data.redis.use-redis-cache}")
    private boolean useRedisCache;

    private <E> String getKeyPrefixFromEntityClass(E entity) {
        if (entity instanceof Category) {
            return AppUtils.KEY_PREFIX_GET_ALL_CATEGORY;
        } else if (entity instanceof Order) {
            return AppUtils.KEY_PREFIX_GET_ALL_ORDER;
        } else {
            return AppUtils.KEY_PREFIX_GET_ALL_PRODUCT;
        }
    }

    @SafeVarargs
    @Override
    public final <K> String getKeyFrom(String keyPrefix, K... keyComponents) {
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(keyPrefix);
        for (K keyComponent : keyComponents) {
            keyBuilder.append(":").append(keyComponent);
        }
        logger.info("Key: " + keyBuilder);
        return keyBuilder.toString();
    }

    @Override
    public void clearCache(T entity) {
        logger.info("run clear method");
        String keyPrefix = getKeyPrefixFromEntityClass(entity);
        logger.info("keyPrefix: " + keyPrefix);
        Set<String> keys = redisTemplate.keys(keyPrefix + "*");

        if (keys != null && !keys.isEmpty()) {
            logger.info("set keys" + keys);
            logger.info(String.valueOf(redisTemplate.delete(keys)));
        }
    }

    @Override
    public <R> R getAll(String key, Class<R> responseType) throws JsonProcessingException {
        if (!useRedisCache) {
            return null;
        }
        String json = (String) redisTemplate.opsForValue().get(key);
        R response = json != null ? redisObjectMapper.readValue(json, responseType) : null;
        String haveData = json != null ? "true" : "false";
        logger.info(String.format("Get cache data of key: %s, have data: %s", key, haveData));
        return response;
    }

    @Override
    public <R> void saveAll(String key, R data) throws JsonProcessingException {
        String json = redisObjectMapper.writeValueAsString(data);
        assert json != null;
        redisTemplate.opsForValue().set(key, json);
        logger.info(String.format("Save cache successfully, key: %s", key));
    }
}
