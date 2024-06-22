package com.duck.controller.location;

import com.duck.dto.location.DistrictResponse;
import com.duck.service.location.DistrictService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "District")
@RequestMapping("/api/v1/districts")
public class DistrictController {
    @Autowired
    private DistrictService districtService;

    @GetMapping("/getAll")
    public ResponseEntity<DistrictResponse> getAll(
            @RequestParam(value = "pageNo", required = false, defaultValue = "${paging.default.page-number}") int pageNo,
            @RequestParam(value = "pageSize", required = false, defaultValue = "${paging.default.page-size}") int pageSize) {
        return ResponseEntity.ok(districtService.getAll(pageNo, pageSize));
    }

    @GetMapping("/getByProvince")
    public ResponseEntity<DistrictResponse> getByProvince(
            @RequestParam(value = "provinceCode") String provinceCode,
            @RequestParam(value = "pageNo", required = false, defaultValue = "${paging.default.page-number}") int pageNo,
            @RequestParam(value = "pageSize", required = false, defaultValue = "${paging.default.page-size}") int pageSize) {
        return ResponseEntity.ok(districtService.getByProvince(provinceCode, pageNo, pageSize));
    }
}
