package com.hdshop.repository.product;

import com.hdshop.entity.product.OptionValue;
import com.hdshop.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OptionValueRepository extends JpaRepository<OptionValue, Long> {
    Optional<OptionValue> findByValueNameAndOption_ProductProductId(String valueName, Long productId);
}
