package com.hdshop.controller.location;

import com.hdshop.dto.location.DistrictResponse;
import com.hdshop.dto.location.WardResponse;
import com.hdshop.service.location.WardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Ward")
@RequestMapping("/api/v1/wards")
public class WardController {
    @Autowired
    private WardService wardService;

    @GetMapping("/getAll")
    public ResponseEntity<WardResponse> getAll(
            @RequestParam(value = "pageNo", required = false, defaultValue = "${paging.default.page-number}") int pageNo,
            @RequestParam(value = "pageSize", required = false, defaultValue = "${paging.default.page-size}") int pageSize) {
        return ResponseEntity.ok(wardService.getAll(pageNo, pageSize));
    }

    @GetMapping("/getByProvince")
    public ResponseEntity<WardResponse> getByProvince(
            @RequestParam(value = "districtCode") String districtCode,
            @RequestParam(value = "pageNo", required = false, defaultValue = "${paging.default.page-number}") int pageNo,
            @RequestParam(value = "pageSize", required = false, defaultValue = "${paging.default.page-size}") int pageSize) {
        return ResponseEntity.ok(wardService.getByDistrict(districtCode, pageNo, pageSize));
    }
}
