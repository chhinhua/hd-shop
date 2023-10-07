package com.hdshop.dtos;

import com.hdshop.entities.Category;
import com.hdshop.entities.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private Long id;

    private String name;

    private String slug;

    private String description;

    private Date createAt;

    private Date updateAt;

    private Long parentId;
}
