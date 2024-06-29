package com.duck.utils.enums;

import lombok.Getter;

@Getter
public enum EProductAnalysisType {
    CLICK("click", "Trỏ vào"),
    VIEW("view", "Xem"),
    ADD_CART("add_cart", "Thêm vào giỏ hàng");

    private final String key;
    private final String value;

    EProductAnalysisType(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Finds the EProductAnalysisType enum based on the provided key.
     *
     * @param key The key to search for.
     * @return The corresponding EProductAnalysisType enum, or null if not found.
     * @throws IllegalArgumentException if the key does not match any enum value.
     */
    public static EProductAnalysisType fromKey(String key) {
        for (EProductAnalysisType type : EProductAnalysisType.values()) {
            if (type.getKey().equalsIgnoreCase(key)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant with key " + key);
    }
}
