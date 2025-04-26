package com.delivery.security;

import com.delivery.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityService {
    public Object getCurrentUser(UserRole expectedRole) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal userPrincipal)) {
            throw new ApiException("Not authenticated", HttpStatus.UNAUTHORIZED);
        }

        if (userPrincipal.getRole() != expectedRole) {
            throw new ApiException("Access denied", HttpStatus.FORBIDDEN);
        }

        return userPrincipal.getUser();
    }
}
