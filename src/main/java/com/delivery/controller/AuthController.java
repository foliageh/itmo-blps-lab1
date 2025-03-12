package com.delivery.controller;

import com.delivery.dto.request.LoginRequest;
import com.delivery.dto.request.RegisterRequest;
import com.delivery.dto.response.AuthResponse;
import com.delivery.model.Courier;
import com.delivery.model.Store;
import com.delivery.security.UserRole;
import com.delivery.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
        Store store = authService.registerStore(
            request.getEmail(),
            request.getName(),
            request.getPassword()
        );
        return Map.of("result", "success");
    }

    @Operation(summary = "Register a new courier")
    @PostMapping("/courier/register")
    public Map<String, String> registerCourier(@Valid @RequestBody RegisterRequest request) {
        Courier courier = authService.registerCourier(
            request.getEmail(),
            request.getName(),
            request.getPassword()
        );
        return Map.of("result", "success");
    }

    @Operation(summary = "Login as a store")
    @PostMapping("/store/login")
    public AuthResponse loginStore(@Valid @RequestBody LoginRequest request) {
        String jwt = authService.loginUser(request.getEmail(), request.getPassword(), UserRole.STORE);
        return new AuthResponse(jwt);
    }

    @Operation(summary = "Login as a courier")
    @PostMapping("/courier/login")
    public AuthResponse loginCourier(@Valid @RequestBody LoginRequest request) {
        String jwt = authService.loginUser(request.getEmail(), request.getPassword(), UserRole.COURIER);
        return new AuthResponse(jwt);
    }
}
