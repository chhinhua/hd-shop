package com.hdshop.repository;

import com.hdshop.entity.OptionValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OptionValueRepository extends JpaRepository<OptionValue, Long> {
    Optional<OptionValue> findByValueNameAndOption_ProductProductId(String valueName, Long productId);
}
