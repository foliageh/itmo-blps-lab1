package com.delivery.service;

import com.delivery.exception.ApiException;
import com.delivery.model.Courier;
import com.delivery.model.Order;
import com.delivery.repository.CourierRepository;
import com.delivery.repository.OrderRepository;
import com.delivery.security.SecurityService;
import com.delivery.security.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourierService {
    private final CourierRepository courierRepository;
    private final OrderRepository orderRepository;
    private final SecurityService securityService;

    public Courier getProfile() {
        return (Courier) securityService.getCurrentUser(UserRole.COURIER);
    }

    public Order getOrder(Long orderId) {
        var courier = (Courier) securityService.getCurrentUser(UserRole.COURIER);
        return getOrderWithCourierAccess(orderId, courier);
    }

    public Page<Order> getOrders(int pageNumber, int pageSize) {
        var courier = (Courier) securityService.getCurrentUser(UserRole.COURIER);
        return orderRepository.findByCourier(courier, PageRequest.of(pageNumber, pageSize));
    }

    public Order deliverOrder(Long orderId) {
        var courier = (Courier) securityService.getCurrentUser(UserRole.COURIER);
        if (courier.getStatus() != Courier.CourierStatus.BUSY)
            throw new ApiException("Courier must be in BUSY status to deliver order", HttpStatus.BAD_REQUEST);

        var order = getOrderWithCourierAccess(orderId, courier);
        if (order.getStatus() != Order.OrderStatus.IN_DELIVERY)
            throw new ApiException("Order must be in IN_DELIVERY status to be delivered", HttpStatus.BAD_REQUEST);

        order.setStatus(Order.OrderStatus.DELIVERED);
        order.setCompletedAt(LocalDateTime.now());

        courier.setStatus(Courier.CourierStatus.NOT_READY);
        courierRepository.save(courier);

        return orderRepository.save(order);
    }

    public Courier makeCourierReady() {
        var courier = (Courier) securityService.getCurrentUser(UserRole.COURIER);
        if (courier.getStatus() == Courier.CourierStatus.BUSY)
            throw new ApiException("Courier is busy", HttpStatus.BAD_REQUEST);

        courier.setStatus(Courier.CourierStatus.READY);
        courier = courierRepository.save(courier);

        tryAssignOrderToCourier(courier);

        return courier;
    }

    @Async
    public void tryAssignOrderToCourier(Courier courier) {
        if (courier.getStatus() != Courier.CourierStatus.READY)
            return;

        Optional<Order> order = orderRepository.findFirstByStatusOrderByCreatedAtAsc(Order.OrderStatus.COLLECTED);
        if (order.isEmpty())
            return;

        courier.setStatus(Courier.CourierStatus.BUSY);
        courier.setLastAssignment(LocalDateTime.now());
        courierRepository.save(courier);

        var orderToAssign = order.get();
        orderToAssign.setCourier(courier);
        orderToAssign.setStatus(Order.OrderStatus.IN_DELIVERY);
        orderRepository.save(orderToAssign);
    }

    @Async
    public void tryAssignOrderToCouriers(Order order) {
        Optional<Courier> courier = courierRepository.findFirstByStatusOrderByLastAssignmentAsc(Courier.CourierStatus.READY);
        if (courier.isEmpty())
            return;

        var availableCourier = courier.get();
        availableCourier.setStatus(Courier.CourierStatus.BUSY);
        availableCourier.setLastAssignment(LocalDateTime.now());
        courierRepository.save(availableCourier);

        order.setCourier(availableCourier);
        order.setStatus(Order.OrderStatus.IN_DELIVERY);
        orderRepository.save(order);
    }

    private Order getOrderWithCourierAccess(Long orderId, Courier courier) {
        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiException("Order not found", HttpStatus.NOT_FOUND));

        if (order.getCourier() == null || !order.getCourier().getId().equals(courier.getId()))
            throw new ApiException("Order not found", HttpStatus.NOT_FOUND);

        return order;
    }
}
