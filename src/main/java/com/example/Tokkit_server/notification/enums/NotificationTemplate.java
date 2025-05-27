package com.example.Tokkit_server.notification.enums;

import lombok.Getter;

@Getter
public enum NotificationTemplate {

    // SYSTEM 알림
    SYSTEM_MAINTENANCE(NotificationCategory.SYSTEM, "시스템 점검 안내", "금일 시스템 점검이 예정되어 있습니다."),

    // PAYMENT 알림
    PAYMENT_SUCCESS(NotificationCategory.PAYMENT, "결제 완료", "%d원이 결제되었습니다."),
    PAYMENT_REFUND(NotificationCategory.PAYMENT, "환불 완료", "%d원이 환불되었습니다."),

    // VOUCHER 알림
    VOUCHER_EXPIRED(NotificationCategory.VOUCHER, "바우처 만료", "[%s] 바우처가 만료되었습니다."),

    // TOKEN 알림
    TOKEN_CONVERTED(NotificationCategory.TOKEN, "토큰 전환 완료", "토큰이 성공적으로 전환되었습니다.");

    private final NotificationCategory category;
    private final String title;
    private final String contentTemplate; // %s, %d 같이 변수를 받을 수 있게

    NotificationTemplate(NotificationCategory category, String title, String contentTemplate) {
        this.category = category;
        this.title = title;
        this.contentTemplate = contentTemplate;
    }
}
