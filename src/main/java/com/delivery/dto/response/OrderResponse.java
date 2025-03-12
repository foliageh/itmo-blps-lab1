package com.delivery.dto.response;

import com.delivery.model.Order;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private String customerEmail;
    private List<Long> orderItemIds;
    private Order.OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private Long storeId;
    private Long courierId;

    public static OrderResponse fromOrder(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setCustomerEmail(order.getCustomerEmail());
        response.setOrderItemIds(order.getOrderItemIds());
        response.setStatus(order.getStatus());
        response.setCreatedAt(order.getCreatedAt());
        response.setCompletedAt(order.getCompletedAt());
        response.setStoreId(order.getStore().getId());
        if (order.getCourier() != null) {
            response.setCourierId(order.getCourier().getId());
        }
        return response;
    }
}
