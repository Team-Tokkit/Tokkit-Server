package com.example.Tokkit_server.transaction.utils;

public class TransactionDisplayFormatter {

    public static String userTokenPayment(String storeName) {
        return String.format("[토큰 결제] %s", storeName);
    }

    public static String userVoucherPayment(String voucherName, String storeName, String userName) {
        return String.format("[바우처 결제] %s, %s에서 '%s' 사용", userName, storeName, voucherName);
    }

    public static String merchantTokenSettlement(String userName) {
        return String.format("[토큰 정산] %s", userName);
    }

    public static String merchantVoucherSettlement(String voucherName, String userName) {
        return String.format("[바우처 정산] %s, '%s' 바우처 정산 완료", userName, voucherName);
    }
}
