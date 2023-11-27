package com.hdshop.service.order.impl;

import com.hdshop.entity.OrderItem;
import com.hdshop.exception.ResourceNotFoundException;
import com.hdshop.repository.OrderItemRepository;
import com.hdshop.service.order.OrderItemService;
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
