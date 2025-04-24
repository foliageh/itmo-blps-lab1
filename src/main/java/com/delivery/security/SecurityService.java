package com.delivery.security;

import com.delivery.exception.ApiException;
import com.delivery.repository.CourierRepository;
import com.delivery.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityService {
    private final CourierRepository courierRepository;
    private final StoreRepository storeRepository;

    public Object getCurrentUser(UserRole expectedRole) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal userPrincipal)) {
            throw new ApiException("User not authenticated", HttpStatus.UNAUTHORIZED);
        }

        if (userPrincipal.getRole() != expectedRole) {
            throw new ApiException("Access denied", HttpStatus.FORBIDDEN);
        }

        if (userPrincipal.getRole() == UserRole.STORE) {
            return storeRepository.findByEmail(userPrincipal.getUsername())
                    .orElseThrow(() -> new ApiException("Store not found", HttpStatus.BAD_REQUEST));
        } else if (userPrincipal.getRole() == UserRole.COURIER) {
            return courierRepository.findByEmail(userPrincipal.getUsername())
                    .orElseThrow(() -> new ApiException("Courier not found", HttpStatus.BAD_REQUEST));
        }

        throw new ApiException("User not found", HttpStatus.BAD_REQUEST);
    }
}
