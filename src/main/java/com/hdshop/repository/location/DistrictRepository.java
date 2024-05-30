package com.hdshop.repository.location;

import com.hdshop.entity.location.District;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {
    Page<District> findAllByProvinceCode(String province_code, Pageable pageable);
}
