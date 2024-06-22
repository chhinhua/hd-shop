package com.duck.service.location;

import com.duck.dto.location.WardResponse;

public interface WardService {
    WardResponse getAll(final int pageNo, final int pageSize);
    WardResponse getByDistrict(final String districtCode, final int pageNo, final int pageSize);
}
