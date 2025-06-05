package com.delivery.security;

import com.delivery.repository.CourierRepository;
import com.delivery.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
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
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var store = storeRepository.findByEmail(email).orElse(null);
        if (store != null)
            return new UserPrincipal(
                    store.getEmail(),
                    store.getPassword(),
                    UserRole.STORE,
                    store
            );

        var courier = courierRepository.findByEmail(email).orElse(null);
        if (courier != null)
            return new UserPrincipal(
                    courier.getEmail(),
                    courier.getPassword(),
                    UserRole.COURIER,
                    courier
            );

        throw new UsernameNotFoundException("User not found");
    }
}
