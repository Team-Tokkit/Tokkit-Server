package com.example.Tokkit_server.voucher.dto.request;

import com.example.Tokkit_server.store.enums.StoreCategory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoucherSearchRequest {
    private StoreCategory storeCategory;
    private String searchKeyword;
    private String sort;
    private String direction;
}
