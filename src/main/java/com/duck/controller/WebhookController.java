package com.duck.controller;

import com.duck.dto.order.OrderStatusPayload;
import com.duck.service.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/webhooks")
public class WebhookController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/order/tracking")
    public ResponseEntity<?> trackOrder(@RequestBody OrderStatusPayload payload){
        orderService.trackingOrder(payload);
        return ResponseEntity.ok("Tracking order status successfully");
    }
}
