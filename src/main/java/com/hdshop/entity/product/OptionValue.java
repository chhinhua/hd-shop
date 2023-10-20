package com.hdshop.entity.product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "option_values")
public class OptionValue {
   @EmbeddedId
    private OptionValueId optionValueId;

    @Column(name = "value_name")
    private String valueName;

    @Column(name = "image_url") // for color
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "option_id", referencedColumnName = "option_id", insertable = false, updatable = false),
            @JoinColumn(name = "product_id", referencedColumnName = "product_id", insertable = false, updatable = false)
    })
    @MapsId("optionValueId")
    private Option option;

    @OneToMany(mappedBy = "optionValue", cascade = CascadeType.ALL)
    private List<SkuValue> skuValues = new ArrayList<>();
}

