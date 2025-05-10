package com.delivery.security;

import com.delivery.model.Courier;
import com.delivery.model.Store;
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
    private final XmlAuthLoader xmlAuthLoader;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        XmlAuthLoader.XmlUserCredentials credentials = xmlAuthLoader.loadUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return switch (credentials.role()) {
            case STORE -> {
                Store store = storeRepository.findByEmail(email)
                        .orElseThrow(() -> new UsernameNotFoundException("Store not found"));
                yield new UserPrincipal(
                        store.getEmail(),
                        credentials.password(),
                        UserRole.STORE,
                        store
                );
            }
            case COURIER -> {
                Courier courier = courierRepository.findByEmail(email)
                        .orElseThrow(() -> new UsernameNotFoundException("Courier not found"));
                yield new UserPrincipal(
                        courier.getEmail(),
                        credentials.password(),
                        UserRole.COURIER,
                        courier
                );
            }
        };
    }
}
