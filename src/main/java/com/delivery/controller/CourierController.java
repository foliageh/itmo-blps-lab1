package com.delivery.controller;

import com.delivery.dto.response.CourierResponse;
import com.delivery.dto.response.OrderResponse;
import com.delivery.service.CourierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/courier")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Courier", description = "Courier operations for managing deliveries")
public class CourierController {
    private final CourierService courierService;

    @Operation(summary = "Get courier's profile",
            description = "Retrieve the authenticated courier's profile information")
    @GetMapping("/profile")
    public CourierResponse getProfile() {
        var courier = courierService.getProfile();
        return CourierResponse.fromCourier(courier);
    }

    @Operation(summary = "Get courier's order by ID",
            description = "Retrieve a specific order assigned to the authenticated courier by its ID")
    @GetMapping("/orders/{orderId}")
    public OrderResponse getOrder(@PathVariable Long orderId) {
        var order = courierService.getOrder(orderId);
        return OrderResponse.fromOrder(order);
    }

    @Operation(summary = "Get courier's orders",
            description = "Retrieve all orders assigned to the authenticated courier")
    @GetMapping("/orders")
    public List<OrderResponse> getOrders() {
        return courierService.getOrders().stream()
            .map(OrderResponse::fromOrder)
            .collect(Collectors.toList());
    }

    @Operation(summary = "Set courier status to ready", 
            description = "Update the authenticated courier's status to ready for accepting deliveries")
    @GetMapping("/ready")
    public CourierResponse makeCourierReady() {
        var courier = courierService.makeCourierReady();
        return CourierResponse.fromCourier(courier);
    }

    @Operation(summary = "Mark order as delivered", 
            description = "Complete the delivery of a specific order")
    @PostMapping("/orders/{orderId}/deliver")
    public OrderResponse deliverOrder(@PathVariable Long orderId) {
        var order = courierService.deliverOrder(orderId);
        return OrderResponse.fromOrder(order);
    }
}
