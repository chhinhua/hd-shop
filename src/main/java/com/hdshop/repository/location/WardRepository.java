package com.hdshop.repository.location;

import com.hdshop.entity.location.Ward;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WardRepository extends JpaRepository<Ward, Long> {
    Page<Ward> findAllByDistrictCode(String districtCode, Pageable pageable);
}
