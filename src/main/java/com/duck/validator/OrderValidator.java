package com.duck.validator;

import com.duck.dto.order.OrderDTO;
import com.duck.dto.order.OrderItemDTO;
import com.duck.entity.ProductSku;
import com.duck.exception.BadCredentialsException;
import com.duck.service.product.ProductSkuService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderValidator {
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private ProductSkuService skuService;

    public void validateCreate(OrderDTO dto) {
        validateSkus(dto.getOrderItems());
    }

    /**
     * Validates the list of {@link OrderItemDTO} by checking the quantity available for each {@link ProductSku}.
     * If any item's quantity exceeds the available quantity, a {@link BadCredentialsException} is thrown.
     *
     * @param items the list of {@link OrderItemDTO} to validate
     * @throws BadCredentialsException if any item's quantity exceeds the available quantity
     */
    private void validateSkus(List<OrderItemDTO> items) {
        items.stream().forEach(item -> {
            ProductSku sku = skuService.findById(item.getSkuId());
            if (item.getQuantity() > sku.getQuantityAvailable()) {
                throw new BadCredentialsException("%s, %s %d %s".formatted(
                        message("out-of-stock"),
                        message("you-can-only-order-up-to"),
                        sku.getQuantityAvailable(),
                        message("product"))
                );
            }
        });
    }

    private String message(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
