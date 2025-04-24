package com.delivery.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.util.List;

@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateOrderRequest {
    @NotBlank(message = "Customer email is required")
    @Email(message = "Invalid email format")
    String customerEmail;

    @NotEmpty(message = "Order items are required")
    List<Long> orderItemIds;

    @NotNull(message = "Store id is required")
    Long storeId;
}
