package com.duck.service.location;

import com.duck.dto.location.DistrictResponse;

public interface DistrictService {
    DistrictResponse getAll(final int pageNo, final int pageSize);
    DistrictResponse getByProvince(final String provinceCode, final int pageNo, final int pageSize);
}
