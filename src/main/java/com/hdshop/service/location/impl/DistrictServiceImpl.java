package com.hdshop.service.location.impl;

import com.hdshop.dto.location.DistrictResponse;
import com.hdshop.entity.location.District;
import com.hdshop.repository.location.DistrictRepository;
import com.hdshop.service.location.DistrictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DistrictServiceImpl implements DistrictService {
    @Autowired
    private DistrictRepository districtRepository;

    @Override
    public DistrictResponse getAll(int pageNo, int pageSize) {
        // Check if pageSize is -1, if so, get all data for one page
        Pageable pageable;
        if (pageSize == -1) {
            // Get all data for one page
            pageable = Pageable.unpaged();
        } else {
            // Follow Pageable instances
            pageable = PageRequest.of(pageNo - 1, pageSize);
        }

        Page<District> districtPage = districtRepository.findAll(pageable);

        // get content for page object
        List<District> districtList = districtPage.getContent();

        // set data to the district response
        DistrictResponse districtResponse = new DistrictResponse();
        districtResponse.setContent(districtList);
        districtResponse.setPageNo(districtPage.getNumber() + 1);
        districtResponse.setPageSize(districtPage.getSize());
        districtResponse.setTotalPages(districtPage.getTotalPages());
        districtResponse.setTotalElements(districtPage.getTotalElements());
        districtResponse.setLast(districtPage.isLast());

        return districtResponse;
    }

    @Override
    public DistrictResponse getByProvince(String provinceCode, int pageNo, int pageSize) {
        // Check if pageSize is -1, if so, get all data for one page
        Pageable pageable;
        if (pageSize == -1) {
            // Get all data for one page
            pageable = Pageable.unpaged();
        } else {
            // Follow Pageable instances
            pageable = PageRequest.of(pageNo - 1, pageSize);
        }

        Page<District> districtPage = districtRepository.findAllByProvinceCode(provinceCode, pageable);

        // get content for page object
        List<District> districtList = districtPage.getContent();

        // set data to the district response
        DistrictResponse districtResponse = new DistrictResponse();
        districtResponse.setContent(districtList);
        districtResponse.setPageNo(districtPage.getNumber() + 1);
        districtResponse.setPageSize(districtPage.getSize());
        districtResponse.setTotalPages(districtPage.getTotalPages());
        districtResponse.setTotalElements(districtPage.getTotalElements());
        districtResponse.setLast(districtPage.isLast());

        return districtResponse;
    }
}
