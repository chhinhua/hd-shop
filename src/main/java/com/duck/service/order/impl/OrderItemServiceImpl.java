package com.duck.service.order.impl;

import com.duck.entity.OrderItem;
import com.duck.exception.ResourceNotFoundException;
import com.duck.repository.OrderItemRepository;
import com.duck.service.order.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;

    @Override
    public OrderItem findById(Long orderItemId) {
        return orderItemRepository.findById(orderItemId).orElseThrow(() ->
                new ResourceNotFoundException("order-item-not-found"));
    }
}
