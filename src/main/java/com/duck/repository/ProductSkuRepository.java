package com.duck.repository;

import com.duck.entity.OptionValue;
import com.duck.entity.ProductSku;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductSkuRepository extends JpaRepository<ProductSku, Long> {
    Optional<ProductSku> findBySkuAndProduct_ProductId(String sku, Long productId);

    @Query("SELECT ps FROM ProductSku ps " +
            "WHERE ps.product.productId = :productId " +
            "AND ps.optionValues = :values")
    Optional<ProductSku> findByProduct_ProductIdAndOptionValues(
            @Param("productId") Long productId,
            @Param("values") List<OptionValue> values
    );

    @Query("SELECT ps FROM ProductSku ps " +
            "JOIN ps.product p " +
            "JOIN ps.optionValues ov " +
            "WHERE p.productId = :productId " +
            "AND ov.valueName IN :valueNames " +
            "GROUP BY ps " +
            "HAVING COUNT(DISTINCT ov) = :valueCount")
    Optional<ProductSku> findByProductIdAndValueNames(@Param("productId") Long productId,
                                                      @Param("valueNames") List<String> valueNames,
                                                      @Param("valueCount") int valueCount);
}



