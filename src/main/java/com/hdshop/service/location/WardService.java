package com.hdshop.service.location;

import com.hdshop.dto.location.WardResponse;

public interface WardService {
    WardResponse getAll(final int pageNo, final int pageSize);
    WardResponse getByDistrict(final String districtCode, final int pageNo, final int pageSize);
}
