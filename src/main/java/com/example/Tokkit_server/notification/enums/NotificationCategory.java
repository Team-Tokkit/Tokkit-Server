package com.example.Tokkit_server.notification.enums;

import lombok.Getter;

@Getter
public enum NotificationCategory {
    SYSTEM,    // 시스템 점검 등
    PAYMENT,   // 결제
    VOUCHER,   // 바우처
    TOKEN,     // 지갑/토큰
}
