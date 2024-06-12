package com.hdshop.listener;

import com.hdshop.entity.BaseEntity; // Assuming a base entity class for Order and Product
import com.hdshop.service.redis.RedisService; // Generic Redis service interface
import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("unchecked") // Suppress unchecked cast warning (explained later)
public class EntityListener<T extends BaseEntity> {
    private final Logger logger = LoggerFactory.getLogger(EntityListener.class);

    @Autowired
    private RedisService<T> redisService; // Use generic Redis service

    public EntityListener() {}

    @PrePersist
    public void prePersist(T entity) {
        logger.info("PrePersist for " + entity.getClass().getSimpleName());
    }

    @PostPersist
    public void postPersist(T entity) {
        logger.info("PostPersist for " + entity.getClass().getSimpleName());
        redisService.clearCache(entity); // Call clearCache with entity
    }

    @PreUpdate
    public void preUpdate(T entity) {
        logger.info("PreUpdate for " + entity.getClass().getSimpleName());
    }

    @PostUpdate
    public void postUpdate(T entity) {
        logger.info("PostUpdate for " + entity.getClass().getSimpleName());
        redisService.clearCache(entity);
    }

    @PreRemove
    public void preRemove(T entity) {
        logger.info("PreRemove for " + entity.getClass().getSimpleName());
    }

    @PostRemove
    public void postRemove(T entity) {
        logger.info("PostRemove for " + entity.getClass().getSimpleName());
        redisService.clearCache(entity);
    }
}