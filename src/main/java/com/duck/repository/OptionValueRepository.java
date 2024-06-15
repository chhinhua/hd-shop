package com.duck.repository;

import com.duck.entity.OptionValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OptionValueRepository extends JpaRepository<OptionValue, Long> {
    Optional<OptionValue> findByValueNameAndOption_ProductProductId(String valueName, Long productId);

    @Query("SELECT ov FROM OptionValue ov " +
            "WHERE ov.option.product.productId = :productId " +
            "AND ov.valueName IN :valueNames")
    List<OptionValue> findByOption_Product_ProductIdAndValueNameIsIn(
            @Param("productId") Long productId,
            @Param("valueNames") List<String> valueNames
    );

}
