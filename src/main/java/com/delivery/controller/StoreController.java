package com.delivery.controller;

import com.delivery.dto.response.OrderResponse;
import com.delivery.dto.response.StoreResponse;
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

    @Operation(summary = "Get store's profile",
            description = "Retrieve the authenticated store's profile information")
    @GetMapping("/profile")
    public StoreResponse getProfile() {
        var store = storeService.getProfile();
        return StoreResponse.fromStore(store);
    }

    @Operation(summary = "Get store's order by ID",
            description = "Retrieve a specific order associated with the store by its ID")
    @GetMapping("/orders/{orderId}")
    public OrderResponse getOrder(@PathVariable Long orderId) {
        var order = storeService.getOrder(orderId);
        return OrderResponse.fromOrder(order);
    }

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
        var order = storeService.collectOrder(orderId);
        return OrderResponse.fromOrder(order);
    }

    @Operation(summary = "Cancel an order",
            description = "Cancel a specific order that hasn't been collected yet")
    @PostMapping("/orders/{orderId}/cancel")
    public OrderResponse cancelOrder(@PathVariable Long orderId) {
        var order = storeService.cancelOrder(orderId);
        return OrderResponse.fromOrder(order);
    }
}
