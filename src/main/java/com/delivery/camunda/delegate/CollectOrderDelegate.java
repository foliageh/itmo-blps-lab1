package com.delivery.camunda.delegate;

import com.delivery.exception.ApiException;
import com.delivery.model.Order;
import com.delivery.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CollectOrderDelegate implements JavaDelegate {

    private final OrderRepository orderRepository;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long orderId = (Long) execution.getVariable("orderId");
        log.info("Collecting order with ID: {}", orderId);
        
        try {
            var order = collectOrder(orderId);
            //execution.setVariable("orderStatus", order.getStatus().toString());
        } catch (Exception e) {
            log.error("Error collecting order {}: {}", orderId, e.getMessage());
            execution.setVariable("errorMessage", e.getMessage());
            throw e;
        }
    }

    public Order collectOrder(Long orderId) {
        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiException("Order not found", HttpStatus.NOT_FOUND));
        order.setStatus(Order.OrderStatus.COLLECTED);
        order = orderRepository.save(order);
        return order;
    }
}
