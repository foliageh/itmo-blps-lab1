package com.delivery.camunda.delegate;

import com.delivery.exception.ApiException;
import com.delivery.model.Courier;
import com.delivery.model.Order;
import com.delivery.repository.CourierRepository;
import com.delivery.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class CloseOrderDelegate implements JavaDelegate {

    private final OrderRepository orderRepository;
    private final CourierRepository courierRepository;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long orderId = (Long) execution.getVariable("orderId");
        log.info("Closing order with ID: {}", orderId);

        try {
            var order = closeOrder(orderId);
        } catch (Exception e) {
            log.error("Error closing order {}: {}", orderId, e.getMessage());
            execution.setVariable("errorMessage", e.getMessage());
            throw e;
        }
    }

    public Order closeOrder(Long orderId) {
        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiException("Order not found", HttpStatus.NOT_FOUND));
        var courier = courierRepository.findById(order.getCourier().getId())
                .orElseThrow(() -> new ApiException("Courier not found", HttpStatus.NOT_FOUND));

        order.setStatus(Order.OrderStatus.DELIVERED);
        order.setCompletedAt(LocalDateTime.now());

        courier.setStatus(Courier.CourierStatus.NOT_READY);
        courierRepository.save(courier);

        return orderRepository.save(order);
    }
}
