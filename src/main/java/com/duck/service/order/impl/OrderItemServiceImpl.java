package com.duck.service.order.impl;

import com.duck.dto.order.OrderItemDTO;
import com.duck.entity.OrderItem;
import com.duck.entity.Product;
import com.duck.exception.ResourceNotFoundException;
import com.duck.repository.OrderItemRepository;
import com.duck.service.order.OrderItemService;
import com.duck.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final ProductService productService;

    @Override
    public OrderItem findById(Long orderItemId) {
        return orderItemRepository.findById(orderItemId).orElseThrow(() ->
                new ResourceNotFoundException("order-item-not-found"));
    }

    @Override
    public OrderItem mapToOrderItem(OrderItemDTO itemDTO) {
        Product product = productService.findById(itemDTO.getProductId());

        // TODO Continite implement
        return OrderItem.builder()
                .quantity(itemDTO.getQuantity())
                .price(itemDTO.getPrice())
                .product(product)
                .build();
    }
}
