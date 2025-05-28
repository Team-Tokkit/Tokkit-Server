package com.example.Tokkit_server.notification.enums;

import lombok.Getter;

@Getter
public enum NotificationTemplate {

    // SYSTEM 알림
    SYSTEM_MAINTENANCE(NotificationCategory.SYSTEM, "시스템 점검 안내", "금일 시스템 점검이 예정되어 있습니다."),

    // TOKEN 알림
    TOKEN_CONVERTED(NotificationCategory.TOKEN, "토큰 전환 완료", "예금 %d원이 토큰으로 전환되었습니다."),
    DEPOSIT_CONVERTED(NotificationCategory.TOKEN, "예금 전환 완료", "토큰 %d TKT가 예금으로 전환되었습니다."),
    /**
     * USER 알림
     */
    TOKEN_PAYMENT_SUCCESS(NotificationCategory.PAYMENT, "토큰으로 결제 완료", "%s에서 %d원을 결제했습니다."),
    VOUCHER_PAYMENT_SUCCESS(NotificationCategory.PAYMENT, "바우처로 결제 완료", "[%s] 바우처로 %s에서 %d원을 결제했습니다."),
    PAYMENT_REFUND(NotificationCategory.PAYMENT, "환불 완료", "%d원이 환불되었습니다."),
    VOUCHER_PURCHASED(NotificationCategory.PAYMENT, "바우처 구매 완료", "[%s] 바우처를 %d원에 구매하였습니다."),
    VOUCHER_EXPIRED(NotificationCategory.PAYMENT, "바우처 만료", "[%s] 바우처가 만료되었습니다."),

    /**
     * MERCHANT 알림
     */
    MERCHANT_VOUCHER_SETTLED(NotificationCategory.PAYMENT, "바우처 정산 완료", "%s님이 [%s] 바우처로 %d원을 결제하여 정산되었습니다."),
    MERCHANT_TOKEN_SETTLED(NotificationCategory.PAYMENT, "토큰 정산 완료", "%s님이 %d원을 토큰으로 결제하여 정산되었습니다.");

    private final NotificationCategory category;
    private final String title;
    private final String contentTemplate; // %s, %d 같이 변수를 받을 수 있게

    NotificationTemplate(NotificationCategory category, String title, String contentTemplate) {
        this.category = category;
        this.title = title;
        this.contentTemplate = contentTemplate;
    }
}
