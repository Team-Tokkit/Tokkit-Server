package com.example.Tokkit_server.wallet.controller;

import com.example.Tokkit_server.global.apiPayload.ApiResponse;
import com.example.Tokkit_server.global.util.IdempotencyManager;
import com.example.Tokkit_server.user.auth.CustomUserDetails;
import com.example.Tokkit_server.voucher_ownership.dto.request.VoucherPaymentRequest;
import com.example.Tokkit_server.wallet.dto.request.*;
import com.example.Tokkit_server.wallet.dto.response.*;
import com.example.Tokkit_server.wallet.service.command.WalletCommandService;
import com.example.Tokkit_server.wallet.service.query.BlockchainQueryService;
import com.example.Tokkit_server.wallet.service.query.WalletQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/wallet")
@RequiredArgsConstructor
@Tag(name = "Wallet", description = "전자지갑 관련 API")
public class WalletController {
    private final WalletQueryService queryService;
    private final WalletCommandService commandService;
    private final BlockchainQueryService blockchainQueryService;
    private final IdempotencyManager idempotencyManager;

    // Controller 변경
    @GetMapping("/balance")
    @Operation(summary = "잔액 조회", description = "사용자 ID로 잔액 조회")
    public ApiResponse<WalletBalanceResponse> getBalance(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.onSuccess(commandService.getWalletBalance(userDetails.getId()));
    }

    @PostMapping("/convert/deposit-to-token")
    @Operation(summary = "예금 ➝ 토큰 전환", description = "예금 잔액을 토큰으로 변환합니다.")
    public ApiResponse<String> convertDepositToToken(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                     @RequestBody DepositToTokenRequest request) {
        queryService.convertDepositToToken(userDetails.getId(),request);
        return ApiResponse.onSuccess("예금에서 토큰으로 변환 완료");
    }

    @PostMapping("/convert/token-to-deposit")
    @Operation(summary = "토큰 ➝ 예금 전환", description = "보유한 토큰을 예금으로 전환합니다.")
    public ApiResponse<String> convertTokenToDeposit(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                     @RequestBody TokenToDepositRequest request) {
        queryService.convertTokenToDeposit(userDetails.getId(),request);
        return ApiResponse.onSuccess("토큰을 예금으로 전환 완료");
    }

    @GetMapping("/transactions")
    @Operation(summary = "거래내역 조회", description = "내 지갑의 거래내역을 시간순으로 조회합니다.")
    public ApiResponse<List<TransactionHistoryResponse>> getTransactionHistory(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.onSuccess(commandService.getTransactionHistory(userDetails.getId()));
    }


    @GetMapping("/transactions/recent")
    @Operation(summary = "최근 거래내역 조회", description = "가장 최근의 거래내역 10건을 조회합니다.")
    public ApiResponse<List<TransactionHistoryResponse>> getRecentTransactions(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.onSuccess(queryService.getRecentTransactions(userDetails.getId()));
    }


    @GetMapping("/transactions/{id}")
    @Operation(summary = "거래 상세 조회", description = "특정 거래 상세 정보를 조회합니다.")
    public ApiResponse<TransactionDetailResponse> getTransactionDetail(@PathVariable Long id) {
        return ApiResponse.onSuccess(queryService.getTransactionDetail(id));
    }

    @GetMapping("/tx/{txHash}")
    @Operation(summary = "txHash 상세 조회", description = "특정 거래에 대해 블록체인 상세 정보를 조회합니다.")
    public ApiResponse<TxDetailResponse> getTxDetail(@PathVariable String txHash) {
        return ApiResponse.onSuccess(blockchainQueryService.getTxHashDetail(txHash));
    }

    @PostMapping("/voucher/purchase")
    @Operation(summary = "바우처 구매", description = "토큰으로 바우처를 구매합니다.")
    public ApiResponse<VoucherPurchaseResponse> purchaseVoucher
           (@RequestBody VoucherPurchaseRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {
        return ApiResponse.onSuccess(idempotencyManager.execute(idempotencyKey, () -> commandService.purchaseVoucher(userDetails.getId(),request)));
    }


    @PostMapping("/pay-with-voucher")
    @Operation(summary = "바우처로 결제", description = "QR 코드를 통해 바우처로 결제를 진행합니다.")
    public ApiResponse<VoucherPaymentResponse> payWithVoucher
           (@RequestBody VoucherPaymentRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {
        return ApiResponse.onSuccess(idempotencyManager.execute(idempotencyKey, () -> commandService.payWithVoucher(userDetails.getId(),request)));
    }


    @PostMapping("/pay-with-token")
    @Operation(summary = "토큰으로 직접 결제", description = "QR 코드를 통해 토큰으로 결제를 진행합니다.")
    public ApiResponse<DirectPaymentResponse> payDirectlyWithToken
             (@RequestBody DirectPaymentRequest request,
              @AuthenticationPrincipal CustomUserDetails userDetails,
              @RequestHeader("Idempotency-Key") String idempotencyKey) {
        return ApiResponse.onSuccess(idempotencyManager.execute(idempotencyKey, () -> commandService.payDirectlyWithToken(userDetails.getId(),request)));
    }


    @GetMapping("/payment-options")
    @Operation(summary = "결제 수단 목록 조회", description = "가맹점에서 사용 가능한 바우처 및 토큰 결제 옵션 목록을 조회합니다.")
    public ApiResponse<List<PaymentOptionResponse>> getPaymentOptions(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestParam Long storeId) {
        return ApiResponse.onSuccess(commandService.getPaymentOptions(userDetails.getId(), storeId));
    }

}