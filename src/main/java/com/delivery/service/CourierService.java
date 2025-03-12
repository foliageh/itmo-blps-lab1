package com.delivery.service;

import com.delivery.exception.ApiException;
import com.delivery.model.Courier;
import com.delivery.model.Order;
import com.delivery.repository.CourierRepository;
import com.delivery.repository.OrderRepository;
import com.delivery.security.SecurityService;
import com.delivery.security.UserRole;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourierService {
    private final CourierRepository courierRepository;
    private final OrderRepository orderRepository;
    private final SecurityService securityService;

    public List<Order> getOrders() {
        Courier courier = (Courier) securityService.getCurrentUser(UserRole.COURIER);
        return orderRepository.findByCourier(courier);
    }

    @Transactional
    public Order deliverOrder(Long orderId) {
        Courier courier = (Courier) securityService.getCurrentUser(UserRole.COURIER);
        if (courier.getStatus() != Courier.CourierStatus.BUSY) {
            throw new ApiException("Courier must be in BUSY status to deliver order", 
                                 HttpStatus.BAD_REQUEST);
        }

        Order order = getOrderWithCourierAccess(orderId, courier);
        if (order.getStatus() != Order.OrderStatus.IN_DELIVERY) {
            throw new ApiException("Order must be in IN_DELIVERY status to be delivered", 
                                 HttpStatus.BAD_REQUEST);
        }

        order.setStatus(Order.OrderStatus.DELIVERED);
        order.setCompletedAt(LocalDateTime.now());

        courier.setStatus(Courier.CourierStatus.NOT_READY);
        courierRepository.save(courier);

        return orderRepository.save(order);
    }

    @Transactional
    public Courier makeCourierReady() {
        Courier courier = (Courier) securityService.getCurrentUser(UserRole.COURIER);
        if (courier.getStatus() == Courier.CourierStatus.BUSY) {
            throw new ApiException("Courier is busy", HttpStatus.BAD_REQUEST);
        }

        courier.setStatus(Courier.CourierStatus.READY);
        courier = courierRepository.save(courier);

        tryAssignOrderToCourier(courier);

        return courier;
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void tryAssignOrderToCourier(Courier courier) {
        if (courier.getStatus() != Courier.CourierStatus.READY) {
            return;
        }

        Optional<Order> order = orderRepository.findFirstByStatusOrderByCreatedAtAsc(Order.OrderStatus.COLLECTED);
        
        if (order.isPresent()) {
            courier.setStatus(Courier.CourierStatus.BUSY);
            courier.setLastAssignment(LocalDateTime.now());
            courierRepository.save(courier);

            Order orderToAssign = order.get();
            orderToAssign.setCourier(courier);
            orderToAssign.setStatus(Order.OrderStatus.IN_DELIVERY);
            orderRepository.save(orderToAssign);
        }
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void tryAssignOrderToCouriers(Order order) {
        Optional<Courier> courier = courierRepository.findFirstByStatusOrderByLastAssignmentAsc(
            Courier.CourierStatus.READY);

        if (courier.isPresent()) {
            Courier availableCourier = courier.get();
            availableCourier.setStatus(Courier.CourierStatus.BUSY);
            availableCourier.setLastAssignment(LocalDateTime.now());
            courierRepository.save(availableCourier);

            order.setCourier(availableCourier);
            order.setStatus(Order.OrderStatus.IN_DELIVERY);
            orderRepository.save(order);
        }
    }

    private Order getOrderWithCourierAccess(Long orderId, Courier courier) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiException("Order not found", HttpStatus.NOT_FOUND));

        if (order.getCourier() == null || !order.getCourier().getId().equals(courier.getId())) {
            throw new ApiException("Access denied", HttpStatus.FORBIDDEN);
        }

        return order;
    }
}
