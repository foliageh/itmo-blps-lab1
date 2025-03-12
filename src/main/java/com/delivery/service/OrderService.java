package com.delivery.service;

import com.delivery.exception.ApiException;
import com.delivery.model.Order;
import com.delivery.model.Store;
import com.delivery.repository.OrderRepository;
import com.delivery.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public Order createOrder(String customerEmail, List<Long> orderItemIds, Long storeId) {
        if (customerEmail == null || customerEmail.isEmpty()) {
            throw new ApiException("Customer email is required", HttpStatus.BAD_REQUEST);
        }
        if (orderItemIds == null || orderItemIds.isEmpty()) {
            throw new ApiException("Order items are required", HttpStatus.BAD_REQUEST);
        }

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ApiException("Store not found", HttpStatus.NOT_FOUND));

        Order order = new Order();
        order.setCustomerEmail(customerEmail);
        order.setOrderItemIds(orderItemIds);
        order.setStore(store);
        order.setStatus(Order.OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());

        return orderRepository.save(order);
    }
}
