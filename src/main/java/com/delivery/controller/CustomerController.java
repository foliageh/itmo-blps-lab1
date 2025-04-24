package com.delivery.controller;

import com.delivery.dto.request.CreateOrderRequest;
import com.delivery.dto.response.OrderResponse;
import com.delivery.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
@Tag(name = "Customer", description = "Customer operations for creating orders")
public class CustomerController {
    private final OrderService orderService;

    @Operation(summary = "Create a new order")
    @PostMapping("/orders")
    public OrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
        var order = orderService.createOrder(
                request.getCustomerEmail(),
                request.getOrderItemIds(),
                request.getStoreId()
        );
        return new OrderResponse(order);
    }
}
