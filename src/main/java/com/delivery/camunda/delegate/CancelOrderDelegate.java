package com.delivery.camunda.delegate;

import com.delivery.exception.ApiException;
import com.delivery.model.Order;
import com.delivery.repository.OrderRepository;
import com.delivery.service.StoreService;
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
public class CancelOrderDelegate implements JavaDelegate {

    private final OrderRepository orderRepository;
    private final StoreService storeService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long orderId = (Long) execution.getVariable("orderId");
        log.info("Cancelling order with ID: {}", orderId);

        try {
            var order = cancelOrder(orderId);
            //execution.setVariable("orderStatus", order.getStatus().toString());
        } catch (Exception e) {
            log.error("Error cancelling order {}: {}", orderId, e.getMessage());
            execution.setVariable("errorMessage", e.getMessage());
            throw e;
        }
    }

    public Order cancelOrder(Long orderId) {
        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiException("Order not found", HttpStatus.NOT_FOUND));
        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setCompletedAt(LocalDateTime.now());
        order = orderRepository.save(order);
        storeService.processRefund(orderId);
        return order;
    }
}
