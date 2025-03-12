package com.delivery.controller;

import com.delivery.dto.response.OrderResponse;
import com.delivery.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Store", description = "Store operations for managing orders")
public class StoreController {
    private final StoreService storeService;

    @Operation(summary = "Get store's orders", 
               description = "Retrieve all orders associated with the authenticated store")
    @GetMapping("/orders")
    public List<OrderResponse> getOrders() {
        return storeService.getOrders().stream()
            .map(OrderResponse::fromOrder)
            .collect(Collectors.toList());
    }

    @Operation(summary = "Mark order as collected")
    @PostMapping("/orders/{orderId}/collect")
    public OrderResponse collectOrder(@PathVariable Long orderId) {
        return OrderResponse.fromOrder(storeService.collectOrder(orderId));
    }

    @Operation(summary = "Cancel an order", 
               description = "Cancel a specific order that hasn't been collected yet")
    @PostMapping("/orders/{orderId}/cancel")
    public OrderResponse cancelOrder(@PathVariable Long orderId) {
        return OrderResponse.fromOrder(storeService.cancelOrder(orderId));
    }
}
