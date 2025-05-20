package com.example.Tokkit_server.wallet.controller;

import com.example.Tokkit_server.global.apiPayload.ApiResponse;
import com.example.Tokkit_server.merchant.auth.CustomMerchantDetails;
import com.example.Tokkit_server.wallet.dto.request.TokenToDepositRequest;
import com.example.Tokkit_server.wallet.dto.response.*;
import com.example.Tokkit_server.wallet.service.command.MerchantWalletCommandService;
import com.example.Tokkit_server.wallet.service.query.MerchantWalletQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchants/wallet")
@RequiredArgsConstructor
@Tag(name = "Merchant Wallet", description = "가맹점주 전자지갑 관련 API")
public class MerchantWalletController {

    private final MerchantWalletCommandService commandService;
    private final MerchantWalletQueryService queryService;

    @GetMapping("/balance")
    @Operation(summary = "잔액 조회", description = "사용자 ID로 잔액 조회")
    public ApiResponse<MerchantWalletBalanceResponse> getBalance(@AuthenticationPrincipal CustomMerchantDetails merchantDetails) {
        return ApiResponse.onSuccess(commandService.getWalletBalance(merchantDetails.getId()));
    }


    @PostMapping("/convert/token-to-deposit")
    @Operation(summary = "토큰 ➝ 예금 전환", description = "보유한 토큰을 예금으로 전환합니다.")
    public ApiResponse<String> convertTokenToDeposit(@AuthenticationPrincipal CustomMerchantDetails merchantDetails,
                                                     @RequestBody TokenToDepositRequest request) {
        queryService.convertTokenToDeposit(merchantDetails.getId(),request);
        return ApiResponse.onSuccess("토큰을 예금으로 전환 완료");
    }

    @GetMapping("/transactions")
    @Operation(summary = "거래내역 조회", description = "내 지갑의 거래내역을 시간순으로 조회합니다.")
    public ApiResponse<List<TransactionHistoryResponse>> getTransactionHistory(@AuthenticationPrincipal CustomMerchantDetails merchantDetails) {
        return ApiResponse.onSuccess(commandService.getTransactionHistory(merchantDetails.getId()));
    }


    @GetMapping("/transactions/recent")
    @Operation(summary = "최근 거래내역 조회", description = "가장 최근의 거래내역 10건을 조회합니다.")
    public ApiResponse<List<TransactionHistoryResponse>> getRecentTransactions(@AuthenticationPrincipal CustomMerchantDetails merchantDetails) {
        return ApiResponse.onSuccess(queryService.getRecentTransactions(merchantDetails.getId()));
    }


    @GetMapping("/transactions/{id}")
    @Operation(summary = "거래 상세 조회", description = "특정 거래 상세 정보를 조회합니다.")
    public ApiResponse<TransactionDetailResponse> getTransactionDetail(@PathVariable Long id) {
        return ApiResponse.onSuccess(queryService.getTransactionDetail(id));
    }
}
