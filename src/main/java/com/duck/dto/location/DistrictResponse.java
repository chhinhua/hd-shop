package com.duck.dto.location;

import com.duck.entity.location.District;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Hidden
@Schema(description = "District Response Information")
public class DistrictResponse {
    @Schema(description = "District Data")
    private List<District> content;

    @Schema(description = "Page Number")
    private int pageNo;

    @Schema(description = "Page Size")
    private int pageSize;

    @Schema(description = "Total district")
    private long totalElements;

    @Schema(description = "Total Pages")
    private int totalPages;

    @Schema(description = "Is it the last page?")
    private boolean last;
}