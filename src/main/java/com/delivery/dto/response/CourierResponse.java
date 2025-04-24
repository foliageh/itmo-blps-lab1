package com.delivery.dto.response;

import com.delivery.model.Courier;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CourierResponse {
    private Long id;
    private String email;
    private String name;
    private Courier.CourierStatus status;
    private LocalDateTime lastAssignment;

    public static CourierResponse fromCourier(Courier courier) {
        return new CourierResponse(
                courier.getId(),
                courier.getEmail(),
                courier.getName(),
                courier.getStatus(),
                courier.getLastAssignment()
        );
    }
}
