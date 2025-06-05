package com.delivery.camunda.delegate;

import com.delivery.exception.ApiException;
import com.delivery.model.Order;
import com.delivery.repository.OrderRepository;
import com.delivery.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateOrderDelegate implements JavaDelegate {

    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String customerEmail = (String) execution.getVariable("customerEmail");
        Long storeId = Long.parseLong(((String) execution.getVariable("storeId")).substring(5));  // ex. store1
        Long itemId = (Long) execution.getVariable("itemId");
        log.info("Creating order for store {}", storeId);

        try {
            var order = createOrder(customerEmail, List.of(itemId), storeId);
            execution.setVariable("orderId", order.getId());
        } catch (Exception e) {
            log.error("Error creating order: {}", e.getMessage());
            execution.setVariable("errorMessage", e.getMessage());
            throw e;
        }
    }

    public Order createOrder(String customerEmail, List<Long> orderItemIds, Long storeId) {
        if (customerEmail == null || customerEmail.isEmpty())
            throw new ApiException("Customer email is required", HttpStatus.BAD_REQUEST);
        if (orderItemIds == null || orderItemIds.isEmpty())
            throw new ApiException("Order items are required", HttpStatus.BAD_REQUEST);

        var store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ApiException("Store not found", HttpStatus.NOT_FOUND));

        var order = new Order();
        order.setCustomerEmail(customerEmail);
        order.setOrderItemIds(orderItemIds);
        order.setStore(store);
        order.setStatus(Order.OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }
}
