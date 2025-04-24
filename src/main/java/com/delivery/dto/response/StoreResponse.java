package com.delivery.dto.response;

import com.delivery.model.Store;
import lombok.Value;

@Value
public class StoreResponse implements EntityApiResponse<Store> {
    Long id;
    String email;
    String name;

    public StoreResponse(Store store) {
        id = store.getId();
        email = store.getEmail();
        name = store.getName();
    }
}
