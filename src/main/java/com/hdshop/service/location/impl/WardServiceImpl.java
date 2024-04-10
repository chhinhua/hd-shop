package com.hdshop.service.location.impl;

import com.hdshop.dto.location.WardResponse;
import com.hdshop.entity.location.Ward;
import com.hdshop.repository.location.WardRepository;
import com.hdshop.service.location.WardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WardServiceImpl implements WardService {
    @Autowired
    private WardRepository wardRepository;

    @Override
    public WardResponse getAll(int pageNo, int pageSize) {
        // Check if pageSize is -1, if so, get all data for one page
        Pageable pageable;
        if (pageSize == -1) {
            // Get all data for one page
            pageable = Pageable.unpaged();
        } else {
            // Follow Pageable instances
            pageable = PageRequest.of(pageNo - 1, pageSize);
        }

        Page<Ward> wardPage = wardRepository.findAll(pageable);

        // get content for page object
        List<Ward> wardList = wardPage.getContent();

        // set data to the ward response
        WardResponse wardResponse = new WardResponse();
        wardResponse.setContent(wardList);
        wardResponse.setPageNo(wardPage.getNumber() + 1);
        wardResponse.setPageSize(wardPage.getSize());
        wardResponse.setTotalPages(wardPage.getTotalPages());
        wardResponse.setTotalElements(wardPage.getTotalElements());
        wardResponse.setLast(wardPage.isLast());

        return wardResponse;
    }

    @Override
    public WardResponse getByDistrict(String districtCode, int pageNo, int pageSize) {
        return null;
    }
}
