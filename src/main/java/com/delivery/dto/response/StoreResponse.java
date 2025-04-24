package com.delivery.dto.response;

import com.delivery.model.Store;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StoreResponse {
    private Long id;
    private String email;
    private String name;

    public static StoreResponse fromStore(Store store) {
        return new StoreResponse(
                store.getId(),
                store.getEmail(),
                store.getName()
        );
    }
}
