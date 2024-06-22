package com.duck.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "option_values")
public class OptionValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long valueId;

    @Column(nullable = false)
    String valueName;

    @Column(name = "image_url") // for color
    String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id")
    Option option;

    @ManyToMany(mappedBy = "optionValues", cascade = CascadeType.ALL)
    List<ProductSku> productSkus = new ArrayList<>();
}

