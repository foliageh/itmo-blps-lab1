package com.delivery.service;

import com.delivery.config.CamundaConfig;
import com.delivery.exception.ApiException;
import com.delivery.model.Courier;
import com.delivery.model.Store;
import com.delivery.repository.CourierRepository;
import com.delivery.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final StoreRepository storeRepository;
    private final CourierRepository courierRepository;
    private final PasswordEncoder passwordEncoder;
    private final CamundaConfig camundaConfig;

    @Transactional
    public Store registerStore(String email, String name, String password) {
        checkEmailRegistered(email);

        var store = new Store();
        store.setEmail(email);
        store.setName(name);
        store.setPassword(passwordEncoder.encode(password));
        store = storeRepository.save(store);

        camundaConfig.createUser("store"+store.getId(), "a", name, "_", null, "store");

        return store;
    }

    @Transactional
    public Courier registerCourier(String email, String name, String password) {
        checkEmailRegistered(email);

        var courier = new Courier();
        courier.setEmail(email);
        courier.setName(name);
        courier.setPassword(passwordEncoder.encode(password));
        courier.setStatus(Courier.CourierStatus.NOT_READY);
        courier = courierRepository.save(courier);

        camundaConfig.createUser("courier"+courier.getId(), "a", name, "_", null, "courier");

        return courier;
    }

    private void checkEmailRegistered(String email) throws ApiException {
        if (storeRepository.existsByEmail(email) || courierRepository.existsByEmail(email))
            throw new ApiException("Email already registered", HttpStatus.BAD_REQUEST);
    }
}
