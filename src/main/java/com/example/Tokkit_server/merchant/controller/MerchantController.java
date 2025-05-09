package com.example.Tokkit_server.merchant.controller;

import com.example.Tokkit_server.global.apiPayload.ApiResponse;
import com.example.Tokkit_server.merchant.dto.request.CreateMerchantRequestDto;
import com.example.Tokkit_server.merchant.dto.response.MerchantResponseDto;
import com.example.Tokkit_server.merchant.service.MerchantEmailService;
import com.example.Tokkit_server.merchant.service.MerchantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/merchants")
@Tag(name = "Merchant", description = "Merchant 관련 API입니다.")
public class MerchantController {

    private final MerchantService merchantService;
    private final MerchantEmailService merchantEmailService;

    @PostMapping("/register")
    @Operation(summary = "회원가입 요청", description = "회원가입 요청을 처리합니다.")
    public ApiResponse<MerchantResponseDto> createMerchant(@RequestBody CreateMerchantRequestDto requestDto) {
        MerchantResponseDto responseDto = merchantService.createMerchant(requestDto);
        return ApiResponse.onSuccess(responseDto);
    }
}
