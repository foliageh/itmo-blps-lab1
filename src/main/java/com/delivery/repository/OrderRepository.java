package com.delivery.repository;

import com.delivery.model.Courier;
import com.delivery.model.Order;
import com.delivery.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStore(Store store);
    List<Order> findByStatus(Order.OrderStatus status);
    List<Order> findByCourier(Courier courier);
    Optional<Order> findFirstByStatusOrderByCreatedAtAsc(Order.OrderStatus status);
}
