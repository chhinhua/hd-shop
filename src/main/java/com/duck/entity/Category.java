package com.duck.entity;

import com.duck.listener.EntityListener;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@Entity
@EntityListeners(EntityListener.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "categories", indexes = {@Index(name = "idx_cate_name", columnList = "name")})
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    String name;

    String slug;

    Boolean isDeleted;

    @Column(columnDefinition = "LONGTEXT")
    String description;

    @CreatedBy
    String createdBy;

    @LastModifiedBy
    String lastModifiedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST)
    List<Category> children = new ArrayList<>();

    @OneToMany(mappedBy = "category", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.LAZY)
    List<Product> products = new ArrayList<>();
}
