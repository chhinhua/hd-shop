package com.hdshop.repository;

import com.hdshop.entity.ProductSku;
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
            "JOIN ps.optionValues ov " +
            "WHERE ps.product.productId = :productId " +
            "AND ov.valueName IN :valueNames")
    List<ProductSku> findByProductIdAndValueNames(@Param("productId") Long productId,
                                                  @Param("valueNames") List<String> valueNames);

    @Query("SELECT ps FROM ProductSku ps " +
            "JOIN ps.optionValues ov " +
            "WHERE ps.product.productId = :productId " +
            "AND ov.valueName IN :valueNames " +
            "GROUP BY ps " +
            "HAVING COUNT(DISTINCT ov.valueName) = :valueCount")
    List<ProductSku> findByProductIdAndValueNames2(@Param("productId") Long productId,
                                                  @Param("valueNames") List<String> valueNames,
                                                  @Param("valueCount") int valueCount);


}



