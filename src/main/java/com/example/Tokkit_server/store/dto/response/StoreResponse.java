package com.example.Tokkit_server.store.dto.response;

import com.example.Tokkit_server.store.entity.Store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StoreResponse {
    private Long id;
    private String storeName;
    private String roadAddress;
    private String newZipcode;


    // JPQL에서 사용할 생성자
    public StoreResponse(Store store) {
        this.id = store.getId();
        this.storeName = store.getStoreName();
        this.roadAddress = store.getRoadAddress();
        this.newZipcode = store.getNewZipcode();
    }

    public static StoreResponse from(Store store) {
       return new StoreResponse(store);
    }
}
