package com.duck.utils.enums;

import com.duck.entity.Product;
import lombok.Getter;

@Getter
public enum EProductStatus {

    IS_ACTIVE("is_active", "Đang hoạt động"),
    UN_ACTIVE("un_active", "Ngừng hoạt động"),
    UNSELLING("unselling", "Chưa đăng bán");

    private final String key;
    private final String value;

    EProductStatus(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Determines the status value of a given product based on its active and selling status.
     * <p>
     * This method evaluates the `isActive` and `isSelling` properties of the product and
     * returns a corresponding status value.
     * </p>
     *
     * @param product the product for which the status value is to be determined
     * @return the status value as a {@code String} based on the following rules:
     *         <ul>
     *           <li>If the product is not active (`isActive` is {@code false}), the method returns {@code UN_ACTIVE.value}.</li>
     *           <li>If the product is active (`isActive` is {@code true}) and is selling (`isSelling` is {@code true}),
     *               the method returns {@code IS_ACTIVE.value}.</li>
     *           <li>If the product is active (`isActive` is {@code true}) but not selling (`isSelling` is {@code false}),
     *               the method returns {@code UNSELLING.value}.</li>
     *         </ul>
     */
    public static String getProductStatusValue(Product product) {
        if (product.getIsActive() == false) {
            return UN_ACTIVE.value;
        }
        if (product.getIsSelling() == true) {
            return IS_ACTIVE.value;
        } else {
            return UNSELLING.value;
        }
    }
}
