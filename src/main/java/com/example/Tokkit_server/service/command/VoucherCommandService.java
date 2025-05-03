package com.example.Tokkit_server.service.command;

public interface VoucherCommandService {

    // 바우처 구매하기
    void purchaseVoucher(Long voucherId, Long userId);

    // 바우처 환불하기
    void refundVoucher(Long voucherId, Long userId);
}
