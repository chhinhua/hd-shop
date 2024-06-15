package com.duck.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "options")
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long optionId;

    @Column(nullable = false)
    String optionName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    Product product;

    @OneToMany(
            mappedBy = "option",
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE}
    )
    List<OptionValue> values = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        for (OptionValue value : values) {
            value.setOption(this);
        }
    }
}
