package com.delivery.repository;

import com.delivery.model.Courier;
import com.delivery.model.Order;
import com.delivery.model.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByStore(Store store, Pageable pageable);
    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);
    Page<Order> findByCourier(Courier courier, Pageable pageable);
    Optional<Order> findFirstByStatusOrderByCreatedAtAsc(Order.OrderStatus status);
}
