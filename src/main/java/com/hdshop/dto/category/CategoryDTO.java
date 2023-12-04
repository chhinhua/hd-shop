package com.hdshop.dto.category;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(hidden = true)
public class CategoryDTO {
    private Long id;

    @NotEmpty
    private String name;

    private String slug;

    private String description;

    private Long productNumber;

    private String createdBy;

    private String lastModifiedBy;

    private String createdDate;

    private String lastModifiedDate;

    private Long parentId;

    private String parentName;
}
