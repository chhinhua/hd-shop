package com.hdshop.service.location;

import com.hdshop.dto.location.DistrictResponse;

public interface DistrictService {
    DistrictResponse getAll(final int pageNo, final int pageSize);
    DistrictResponse getByProvince(final String provinceCode, final int pageNo, final int pageSize);
}
