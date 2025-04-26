package com.delivery.service;

import com.delivery.exception.ApiException;
import com.delivery.model.Courier;
import com.delivery.model.Store;
import com.delivery.repository.CourierRepository;
import com.delivery.repository.StoreRepository;
import com.delivery.security.UserRole;
import com.delivery.security.XmlAuthWriter;
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
    private final XmlAuthWriter xmlAuthWriter;

    @Transactional
    public Store registerStore(String email, String name, String password) {
        if (xmlAuthWriter.userExists(email)) {
            throw new ApiException("Email already registered", HttpStatus.BAD_REQUEST);
        }

        String encodedPassword = passwordEncoder.encode(password);

        var store = new Store();
        store.setEmail(email);
        store.setName(name);
        store.setPassword(encodedPassword);
        store = storeRepository.save(store);

        xmlAuthWriter.addUser(
                email,
                encodedPassword,
                UserRole.STORE
        );

        return store;
    }

    @Transactional
    public Courier registerCourier(String email, String name, String password) {
        if (xmlAuthWriter.userExists(email)) {
            throw new ApiException("Email already registered", HttpStatus.BAD_REQUEST);
        }

        String encodedPassword = passwordEncoder.encode(password);

        var courier = new Courier();
        courier.setEmail(email);
        courier.setName(name);
        courier.setPassword(encodedPassword);
        courier.setStatus(Courier.CourierStatus.NOT_READY);
        courier = courierRepository.save(courier);

        xmlAuthWriter.addUser(
                email,
                encodedPassword,
                UserRole.COURIER
        );

        return courier;
    }
}
