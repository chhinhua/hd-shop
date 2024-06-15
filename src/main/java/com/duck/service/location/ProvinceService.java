package com.duck.service.location;

import com.duck.dto.location.ProvinceResponse;

public interface ProvinceService {
    ProvinceResponse getAll(final int pageNo, final int pageSize);
}
