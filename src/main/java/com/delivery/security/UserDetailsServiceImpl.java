package com.delivery.security;

import com.delivery.exception.ApiException;
import com.delivery.model.Courier;
import com.delivery.model.Store;
import com.delivery.repository.CourierRepository;
import com.delivery.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final StoreRepository storeRepository;
    private final CourierRepository courierRepository;

    @Override
    public UserDetails loadUserByUsername(String emailPlusRole) throws UsernameNotFoundException {
        String email = emailPlusRole.substring(0, emailPlusRole.lastIndexOf(" "));
        UserRole role = UserRole.valueOf(emailPlusRole.substring(emailPlusRole.lastIndexOf(" ") + 1));
        
        switch (role) {
            case STORE -> {
                Store store = storeRepository.findByEmail(email)
                        .orElseThrow(() -> new ApiException("Store not found", HttpStatus.NOT_FOUND));
                return new UserPrincipal(store.getEmail(), store.getPassword(), UserRole.STORE);
            }
            case COURIER -> {
                Courier courier = courierRepository.findByEmail(email)
                        .orElseThrow(() -> new ApiException("Courier not found", HttpStatus.NOT_FOUND));
                return new UserPrincipal(courier.getEmail(), courier.getPassword(), UserRole.COURIER);
            }
            default -> throw new ApiException("Invalid role", HttpStatus.BAD_REQUEST);
        }
    }
}
