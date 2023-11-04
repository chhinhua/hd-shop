package com.hdshop.dto.category;

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

    private String createdBy;

    private String lastModifiedBy;

    private Date createdDate;

    private Date lastModifiedDate;

    private Long parentId;
}
