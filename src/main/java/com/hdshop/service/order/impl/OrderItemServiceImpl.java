package com.hdshop.service.order.impl;

import com.hdshop.dto.order.OrderItemDTO;
import com.hdshop.entity.OrderItem;
import com.hdshop.entity.Product;
import com.hdshop.exception.ResourceNotFoundException;
import com.hdshop.repository.OrderItemRepository;
import com.hdshop.service.order.OrderItemService;
import com.hdshop.service.product.ProductService;
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
