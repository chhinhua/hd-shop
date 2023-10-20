package com.hdshop.repository.product;

import com.hdshop.entity.product.SkuValue;
import com.hdshop.entity.product.SkuValueId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkuValueRepository extends JpaRepository<SkuValue, SkuValueId> {
}
