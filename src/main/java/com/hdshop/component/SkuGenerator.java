package com.hdshop.component;

import com.github.slugify.Slugify;
import com.hdshop.entity.OptionValue;

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
                String skuValue = replaceVietnameseCharacters(value.getValueName());
                skuBuilder.append("-");
                skuBuilder.append(skuValue);
            }
        }

        Slugify slugify = new Slugify();
        return slugify.slugify(skuBuilder.toString());
    }

    private static String replaceVietnameseCharacters(String input) {
        String replacedString = input
                .replaceAll("đ", "d")
                .replaceAll("Đ", "D");

        return replacedString;
    }
}
