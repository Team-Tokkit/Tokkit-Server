package com.example.Tokkit_server.voucher.dto.request;

import com.example.Tokkit_server.store.enums.StoreCategory;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoucherSearchRequest {
    private StoreCategory storeCategory;    // 카테고리(CULTURE, JOB)

    private String searchKeyword;   // 검색어
    private String sort;    // 금액 높은순, 낮은순, 최신순
    private String direction;
}
