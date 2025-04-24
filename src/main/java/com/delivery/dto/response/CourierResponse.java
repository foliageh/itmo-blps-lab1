package com.delivery.dto.response;

import com.delivery.model.Courier;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class CourierResponse implements EntityApiResponse<Courier> {
    Long id;
    String email;
    String name;
    Courier.CourierStatus status;
    LocalDateTime lastAssignment;

    public CourierResponse(Courier courier) {
        id = courier.getId();
        email = courier.getEmail();
        name = courier.getName();
        status = courier.getStatus();
        lastAssignment = courier.getLastAssignment();
    }
}
