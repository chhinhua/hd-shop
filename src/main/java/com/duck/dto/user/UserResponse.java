package com.duck.dto.user;

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
@Schema(description = "User Response Information")
public class UserResponse {
    @Schema(description = "User Content")
    private List<UserDTO> content;

    @Schema(description = "Page Number")
    private int pageNo;

    @Schema(description = "Page Size")
    private int pageSize;

    @Schema(description = "Total category")
    private long totalElements;

    @Schema(description = "Total Pages")
    private int totalPages;

    @Schema(description = "Is it the last page?")
    private boolean last;
}
