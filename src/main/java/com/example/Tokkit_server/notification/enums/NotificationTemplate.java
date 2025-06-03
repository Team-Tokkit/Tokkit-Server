package com.example.Tokkit_server.notification.enums;

import lombok.Getter;

@Getter
public enum NotificationTemplate {

    // SYSTEM 알림
    SYSTEM_MAINTENANCE(NotificationCategory.SYSTEM, "시스템 점검 안내", "금일 시스템 점검이 예정되어 있습니다.", true, true),

    // TOKEN 알림
    TOKEN_CONVERTED(NotificationCategory.TOKEN, "토큰 전환 완료", "예금 %d원이 토큰으로 전환되었습니다.", false, true),
    TOKEN_AUTO_CONVERTED(NotificationCategory.TOKEN, "토큰 자동 전환 완료", "예금 %d원이 토큰으로 전환되었습니다.", false, true),
    DEPOSIT_CONVERTED(NotificationCategory.TOKEN, "예금 전환 완료", "토큰 %d TKT가 예금으로 전환되었습니다.", false, true),
    /**
     * USER 알림
     */
    TOKEN_PAYMENT_SUCCESS(NotificationCategory.PAYMENT, "토큰으로 결제 완료", "%s에서 %d원을 결제했습니다.", false, true),
    VOUCHER_PAYMENT_SUCCESS(NotificationCategory.PAYMENT, "바우처로 결제 완료", "[%s] 바우처로 %s에서 %d원을 결제했습니다.", false, true),
    PAYMENT_REFUND(NotificationCategory.PAYMENT, "환불 완료", "%d원이 환불되었습니다.", false, true),
    VOUCHER_PURCHASED(NotificationCategory.PAYMENT, "바우처 구매 완료", "[%s] 바우처를 %d원에 구매하였습니다.", false, true),
    VOUCHER_EXPIRED(NotificationCategory.PAYMENT, "바우처 만료", "[%s] 바우처가 만료되었습니다.", true, true),

    /**
     * MERCHANT 알림
     */
    MERCHANT_VOUCHER_SETTLED(NotificationCategory.PAYMENT, "바우처 정산 완료", "%s님이 [%s] 바우처로 %d원을 결제하여 정산되었습니다.", false, true),
    MERCHANT_TOKEN_SETTLED(NotificationCategory.PAYMENT, "토큰 정산 완료", "%s님이 %d원을 토큰으로 결제하여 정산되었습니다.", false, true);

    private final NotificationCategory category;
    private final String title;
    private final String contentTemplate; // %s, %d 같이 변수를 받을 수 있게
    private final boolean sendSse;
    private final boolean sendEmail;

    NotificationTemplate(NotificationCategory category, String title, String contentTemplate, boolean sendSse, boolean sendEmail) {
        this.category = category;
        this.title = title;
        this.contentTemplate = contentTemplate;
        this.sendSse = sendSse;
        this.sendEmail = sendEmail;
    }

    public static NotificationTemplate from(String title) {
        for (NotificationTemplate template : NotificationTemplate.values()) {
            if (template.getTitle().equals(title)) {
                return template;
            }
        }
        return null;
    }
}
