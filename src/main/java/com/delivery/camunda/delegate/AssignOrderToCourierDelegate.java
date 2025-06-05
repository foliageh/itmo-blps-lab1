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
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AssignOrderToCourierDelegate implements JavaDelegate {

    private final OrderRepository orderRepository;
    private final CourierRepository courierRepository;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long courierId = Long.parseLong(((String) execution.getVariable("courierId")).substring(7));  // ex. courier1
        log.info("Trying to assign order to courier {}", courierId);

        try {
            var order = tryAssignOrderToCourier(courierId);
            execution.setVariable("orderId", order != null ? order.getId() : null);
        } catch (Exception e) {
            log.error("Error assigning order to courier {}: {}", courierId, e.getMessage());
            execution.setVariable("errorMessage", e.getMessage());
            throw e;
        }
    }

    public Order tryAssignOrderToCourier(Long courierId) {
        var courier = courierRepository.findById(courierId)
                .orElseThrow(() -> new ApiException("Courier not found", HttpStatus.NOT_FOUND));

        Optional<Order> order = orderRepository.findFirstByStatusOrderByCreatedAtAsc(Order.OrderStatus.COLLECTED);
        if (order.isEmpty())
            return null;

        courier.setStatus(Courier.CourierStatus.BUSY);
        courier.setLastAssignment(LocalDateTime.now());
        courierRepository.save(courier);

        var orderToAssign = order.get();
        orderToAssign.setCourier(courier);
        orderToAssign.setStatus(Order.OrderStatus.IN_DELIVERY);
        orderRepository.save(orderToAssign);
        return orderToAssign;
    }
}
