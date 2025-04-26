package com.delivery.service;

import com.delivery.exception.ApiException;
import com.delivery.model.Courier;
import com.delivery.model.Store;
import com.delivery.repository.CourierRepository;
import com.delivery.repository.StoreRepository;
import com.delivery.security.JwtTokenProvider;
import com.delivery.security.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final StoreRepository storeRepository;
    private final CourierRepository courierRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public Store registerStore(String email, String name, String password) {
        if (storeRepository.findByEmail(email).isPresent())
            throw new ApiException("Email already registered as a store", HttpStatus.BAD_REQUEST);

        var store = new Store();
        store.setEmail(email);
        store.setName(name);
        store.setPassword(passwordEncoder.encode(password));

        return storeRepository.save(store);
    }

    @Transactional
    public Courier registerCourier(String email, String name, String password) {
        if (courierRepository.findByEmail(email).isPresent())
            throw new ApiException("Email already registered as a courier", HttpStatus.BAD_REQUEST);

        var courier = new Courier();
        courier.setEmail(email);
        courier.setName(name);
        courier.setPassword(passwordEncoder.encode(password));
        courier.setStatus(Courier.CourierStatus.NOT_READY);

        return courierRepository.save(courier);
    }

    public String loginUser(String email, String password, UserRole role) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email + " " + role.name(), password)
        );
        return tokenProvider.generateToken(authentication, role);
    }
}
