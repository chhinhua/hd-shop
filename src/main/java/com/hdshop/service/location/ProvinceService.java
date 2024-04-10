package com.hdshop.service.location;

import com.hdshop.dto.location.ProvinceResponse;

public interface ProvinceService {
    ProvinceResponse getAll(final int pageNo, final int pageSize);
}
