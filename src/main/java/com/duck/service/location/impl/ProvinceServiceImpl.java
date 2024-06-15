package com.duck.service.location.impl;

import com.duck.dto.location.ProvinceResponse;
import com.duck.entity.location.Province;
import com.duck.repository.location.ProvinceRepository;
import com.duck.service.location.ProvinceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProvinceServiceImpl implements ProvinceService {
    private final ProvinceRepository provinceRepository;

    public ProvinceServiceImpl(ProvinceRepository provinceRepository) {
        this.provinceRepository = provinceRepository;
    }

    @Override
    public ProvinceResponse getAll(int pageNo, int pageSize) {
        // Check if pageSize is -1, if so, get all data for one page
        Pageable pageable;
        if (pageSize == -1) {
            // Get all data for one page
            pageable = Pageable.unpaged();
        } else {
            // Follow Pageable instances
            pageable = PageRequest.of(pageNo - 1, pageSize);
        }

        Page<Province> provincePage = provinceRepository.findAll(pageable);

        // get content for page object
        List<Province> provinceList = provincePage.getContent();

        // set data to the product response
        ProvinceResponse provinceResponse = new ProvinceResponse();
        provinceResponse.setContent(provinceList);
        provinceResponse.setPageNo(provincePage.getNumber() + 1);
        provinceResponse.setPageSize(provincePage.getSize());
        provinceResponse.setTotalPages(provincePage.getTotalPages());
        provinceResponse.setTotalElements(provincePage.getTotalElements());
        provinceResponse.setLast(provincePage.isLast());

        return provinceResponse;
    }
}
