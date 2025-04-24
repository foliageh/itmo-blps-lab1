package com.delivery.dto.response;

import lombok.Value;

@Value
public class AuthResponse {
    String token;

    public AuthResponse(String token) {
        this.token = token;
    }
}
