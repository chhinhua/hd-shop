package com.hdshop.service.redis;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface RedisService<T> {
    /**
     * Clears the cache for the given entity.
     * Specific implementations should handle cache clearing logic for their entity types.
     * @param entity the entity for which to clear the cache
     */
    void clearCache(T entity);

    /**
     * Retrieves data from Redis for the given key and deserializes it to the specified type.
     * @param key the cache key
     * @param responseType the type of the data to deserialize to
     * @param <R> the type of the data to deserialize to
     * @return the deserialized data or null if not found
     * @throws JsonProcessingException if there's an error during JSON parsing
     */
    <R> R getAll(String key, Class<R> responseType) throws JsonProcessingException;

    /**
     * Saves data to Redis after serializing it to JSON.
     * @param key the cache key
     * @param data the object to serialize and store
     * @param <R> the type of the data to serialize
     * @throws JsonProcessingException if there's an error during JSON serialization
     */
    <R> void saveAll(String key, R data) throws JsonProcessingException;

    /**
     * Generate redisKey for retreve or save redis data
     * @param keyPrefix
     * @param keyComponents
     * @return redisKey
     * @param <K>
     */
    <K> String getKeyFrom(String keyPrefix, K... keyComponents);
}
