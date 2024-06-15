package com.duck.controller.location;

import com.duck.dto.location.ProvinceResponse;
import com.duck.service.location.ProvinceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Province")
@RestController
@RequestMapping("/api/v1/provinces")
public class ProvinceController {
    @Autowired
    private ProvinceService provinceService;

    @GetMapping("/getAll")
    public ResponseEntity<ProvinceResponse> getAll(
            @RequestParam(value = "pageNo", required = false, defaultValue = "${paging.default.page-number}") int pageNo,
            @RequestParam(value = "pageSize", required = false, defaultValue = "${paging.default.page-size}") int pageSize) {
        return ResponseEntity.ok(provinceService.getAll(pageNo, pageSize));
    }
}
