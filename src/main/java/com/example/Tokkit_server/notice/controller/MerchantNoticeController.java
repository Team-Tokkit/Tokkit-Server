package com.example.Tokkit_server.notice.controller;

import com.example.Tokkit_server.global.apiPayload.ApiResponse;
import com.example.Tokkit_server.notice.dto.response.NoticeResponseDto;
import com.example.Tokkit_server.notice.service.query.NoticeQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/merchants/notice")
@RequiredArgsConstructor
@Tag(name = "Merchant Notice", description = "가맹점주 공지사항 관련 API")
public class MerchantNoticeController {
    private final NoticeQueryService noticeQueryService;

    @GetMapping
    @Operation(summary = "공지사항 목록(전체) 조회", description = "공지사항 전체 목록을 조회하는 API입니다.")
    public ApiResponse<Page<NoticeResponseDto>> getNotices(
            @RequestParam(defaultValue = "0") int page
    ) {
        return ApiResponse.onSuccess(noticeQueryService.getNotices(page));
    }

    @GetMapping("/{noticeId}")
    @Operation(summary = "공지사항 상세 조회", description = "공지사항 상세를 조회하는 API입니다.")
    public ApiResponse<NoticeResponseDto> getNotice(@PathVariable Long noticeId) {
        return ApiResponse.onSuccess(noticeQueryService.getNotice(noticeId));
    }
}
