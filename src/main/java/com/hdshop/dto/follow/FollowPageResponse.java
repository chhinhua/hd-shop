package com.hdshop.dto.follow;

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
@Schema(description = "Wishlist Response Information")
public class FollowPageResponse {
    @Schema(description = "Follow Content")
    private List<FollowDTO> content;

    @Schema(description = "Page Number")
    private int pageNo;

    @Schema(description = "Page Size")
    private int pageSize;

    @Schema(description = "Total products available")
    private long totalElements;

    @Schema(description = "Total Pages")
    private int totalPages;

    @Schema(description = "Is it the last page?")
    private boolean last;
}
