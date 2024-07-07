package com.duck.component;

import com.duck.utils.AppUtils;
import com.github.slugify.Slugify;
import com.duck.entity.OptionValue;

import java.util.List;

public class SkuGenerator {

    /**
     * Generate SKU string value for productSku object
     * @param productId
     * @param optionValues
     * @return sku string
     */
    public static String generateSku(Long productId, List<OptionValue> optionValues) {
        StringBuilder skuBuilder = new StringBuilder();
        skuBuilder.append("SKU-");
        skuBuilder.append(productId);

        for (OptionValue value : optionValues) {
            if (value.getValueName() != null) {
                String skuValue = AppUtils.replaceVietnameseCharacters(value.getValueName());
                skuBuilder.append("-");
                skuBuilder.append(skuValue);
            }
        }

        Slugify slugify = new Slugify();
        return slugify.slugify(skuBuilder.toString());
    }
}
