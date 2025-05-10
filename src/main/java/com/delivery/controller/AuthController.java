package com.delivery.controller;

import com.delivery.dto.request.RegisterRequest;
import com.delivery.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication endpoints for stores and couriers")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Register a new store")
    @PostMapping("/store/register")
    public Map<String, String> registerStore(@Valid @RequestBody RegisterRequest request) {
        var store = authService.registerStore(
            request.getEmail(),
            request.getName(),
            request.getPassword()
        );
        return Map.of("result", "success");
    }

    @Operation(summary = "Register a new courier")
    @PostMapping("/courier/register")
    public Map<String, String> registerCourier(@Valid @RequestBody RegisterRequest request) {
        var courier = authService.registerCourier(
            request.getEmail(),
            request.getName(),
            request.getPassword()
        );
        return Map.of("result", "success");
    }
}
