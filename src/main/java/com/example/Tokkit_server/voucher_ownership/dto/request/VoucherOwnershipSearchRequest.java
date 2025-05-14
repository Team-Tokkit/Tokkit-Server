package com.example.Tokkit_server.voucher_ownership.dto.request;

import com.example.Tokkit_server.store.enums.StoreCategory;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoucherOwnershipSearchRequest {
    private StoreCategory storeCategory;
    private String searchKeyword;
    private String sort;
    private String direction;
    private Long userId;
}
