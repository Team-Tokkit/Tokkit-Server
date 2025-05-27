package com.example.Tokkit_server.voucher_ownership.enums;

public enum VoucherOwnershipStatus {
    AVAILABLE, // 사용 가능
    USED,      // 전액 사용
    EXPIRED,   // 유효기간 만료
    CANCELLED,  // 사용자 또는 관리자에 의해 취소
    DELETED;   // 유저가 직접 삭제
}