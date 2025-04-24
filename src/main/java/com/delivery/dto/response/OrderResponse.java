package com.delivery.dto.response;

import com.delivery.model.Order;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
public class OrderResponse implements EntityApiResponse<Order> {
    Long id;
    String customerEmail;
    List<Long> orderItemIds;
    Order.OrderStatus status;
    LocalDateTime createdAt;
    LocalDateTime completedAt;
    Long storeId;
    Long courierId;

    public OrderResponse(Order order) {
        id = order.getId();
        customerEmail = order.getCustomerEmail();
        orderItemIds = order.getOrderItemIds();
        status = order.getStatus();
        createdAt = order.getCreatedAt();
        completedAt = order.getCompletedAt();
        storeId = order.getStore().getId();
        courierId = order.getCourier() != null ? order.getCourier().getId() : null;
    }
}
