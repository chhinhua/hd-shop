package com.hdshop.repository.product;

import com.hdshop.entity.product.OptionValue;
import com.hdshop.entity.product.OptionValueId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionValueRepository extends JpaRepository<OptionValue, OptionValueId> {
}
