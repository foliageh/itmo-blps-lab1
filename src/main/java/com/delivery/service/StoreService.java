package com.delivery.service;

import com.delivery.exception.ApiException;
import com.delivery.model.Order;
import com.delivery.model.Store;
import com.delivery.repository.OrderRepository;
import com.delivery.security.SecurityService;
import com.delivery.security.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final OrderRepository orderRepository;
    private final CourierService courierService;
    private final SecurityService securityService;

    public Store getProfile() {
        return (Store) securityService.getCurrentUser(UserRole.STORE);
    }

    public Order getOrder(Long orderId) {
        var store = (Store) securityService.getCurrentUser(UserRole.STORE);
        return getOrderWithStoreAccess(orderId, store);
    }

    public List<Order> getOrders() {
        var store = (Store) securityService.getCurrentUser(UserRole.STORE);
        return orderRepository.findByStore(store);
    }

    public Order collectOrder(Long orderId) {
        var store = (Store) securityService.getCurrentUser(UserRole.STORE);
        var order = getOrderWithStoreAccess(orderId, store);

        if (order.getStatus() != Order.OrderStatus.CREATED)
            throw new ApiException("Order must be in CREATED status to be collected",  HttpStatus.BAD_REQUEST);

        order.setStatus(Order.OrderStatus.COLLECTED);
        order = orderRepository.save(order);

        courierService.tryAssignOrderToCouriers(order);

        return order;
    }

    public Order cancelOrder(Long orderId) {
        var store = (Store) securityService.getCurrentUser(UserRole.STORE);
        var order = getOrderWithStoreAccess(orderId, store);

        if (order.getStatus() != Order.OrderStatus.CREATED)
            throw new ApiException("Cannot cancel order in current status: " + order.getStatus(), HttpStatus.BAD_REQUEST);

        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setCompletedAt(LocalDateTime.now());
        order = orderRepository.save(order);

        processRefund(orderId);
        
        return order;
    }

    @Async
    protected void processRefund(Long orderId) {
        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiException("Order not found", HttpStatus.NOT_FOUND));

        try {
            // Simulate refund processing
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Refund processing interrupted", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        order.setStatus(Order.OrderStatus.REFUNDED);
        order.setCompletedAt(LocalDateTime.now());
        orderRepository.save(order);
    }

    private Order getOrderWithStoreAccess(Long orderId, Store store) {
        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiException("Order not found", HttpStatus.NOT_FOUND));

        if (!order.getStore().getId().equals(store.getId()))
            throw new ApiException("Order not found", HttpStatus.NOT_FOUND);

        return order;
    }
}
