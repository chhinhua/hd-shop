package com.duck.dto.product;

import lombok.Getter;

import java.util.List;

@Getter
public class AddInventoryRequest {
    private Long productId;
    private List<SkuRequest> skus;

    @Getter
    public static class SkuRequest {
        private Long skuId;
        private Integer addNumber;
    }
}
