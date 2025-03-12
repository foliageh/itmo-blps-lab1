package com.delivery.repository;

import com.delivery.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findByEmail(String email);
    boolean existsByEmail(String email);
}
