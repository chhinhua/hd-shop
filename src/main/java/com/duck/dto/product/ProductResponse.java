package com.duck.dto.product;

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
@Schema(description = "Product Response Information")
public class ProductResponse {
    @Schema(description = "Product Content")
    private List<ProductDTO> content;

    @Schema(description = "Page Number")
    private int pageNo;

    @Schema(description = "Page Size")
    private int pageSize;

    @Schema(description = "Total Product")
    private long totalElements;

    @Schema(description = "Total Pages")
    private int totalPages;

    @Schema(description = "Is it the last page?")
    private boolean last;

    @Schema(description = "Is it the total element in last page?")
    private long lastPageSize;
}
