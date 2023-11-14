package com.hdshop.controller;

import com.hdshop.dto.order.OrderDTO;
import com.hdshop.dto.order.OrderResponse;
import com.hdshop.service.order.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Tag(name = "Order")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "Create new order by list cartItem")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/create")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderDTO orderDTO, Principal principal) {
        OrderResponse newOrder = orderService.addOrder(orderDTO, principal);
        return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
    }

    @Operation(summary = "Create create order from user cart")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping
    public ResponseEntity<OrderResponse> createOrderFromCart(@Valid @RequestBody OrderDTO orderDTO, Principal principal) {
        OrderResponse newOrder = orderService.addOrderFromUserCart(orderDTO, principal);
        return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
    }

    @Operation(summary = "Delete order by id")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @SecurityRequirement(name = "Bear Authentication")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrderById(id);
        return ResponseEntity.ok("Order deleted successfully");
    }

    @Operation(summary = "Get single order by id")
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        OrderDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @Operation(
            summary = "Update order status by id",
            description = "Update order status by one of values {CANCELED, DELIVERED, ,CANCELED, PROCESSING, ORDERED}"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @SecurityRequirement(name = "Bear Authentication")
    @PutMapping("{id}/status")
    public ResponseEntity<OrderDTO> updateStatus(@PathVariable Long id,
                                                 @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateStatus(id, status));
    }

    @Operation(summary = "Get list order of user by username")
    @GetMapping("/user")
    public ResponseEntity<List<OrderDTO>> getOrdersByUsername(@RequestParam String username) {
        return ResponseEntity.ok(orderService.getOrdersByUsername(username));
    }

    @Operation(summary = "Get list order of user by userId")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }
    // TODO trước mắt chỉ cho thanh toán online (vnpay)
}
