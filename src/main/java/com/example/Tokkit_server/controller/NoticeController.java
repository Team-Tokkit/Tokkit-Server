package com.example.Tokkit_server.controller;

import com.example.Tokkit_server.apiPayload.ApiResponse;
import com.example.Tokkit_server.dto.notices.NoticeRequestDto;
import com.example.Tokkit_server.dto.notices.NoticeResponseDto;
import com.example.Tokkit_server.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping
    public ApiResponse<Long> createNotice(@RequestBody NoticeRequestDto requestDto) {
        return ApiResponse.onSuccess(noticeService.createNotice(requestDto));
    }

    @GetMapping
    public ApiResponse<Page<NoticeResponseDto>> getNotices(
            @RequestParam(defaultValue = "0") int page
    ) {
        return ApiResponse.onSuccess(noticeService.getNotices(page));
    }

    @GetMapping("/{noticeId}")
    public ApiResponse<NoticeResponseDto> getNotice(@PathVariable Long noticeId) {
        return ApiResponse.onSuccess(noticeService.getNotice(noticeId));
    }

    @PutMapping("/{noticeId}")
    public ApiResponse<Void> updateNotice(@PathVariable Long noticeId, @RequestBody NoticeRequestDto requestDto) {
        noticeService.updateNotice(noticeId, requestDto);
        return ApiResponse.onSuccess(null);
    }

    @PostMapping("/{noticeId}")
    public ApiResponse<Void> deleteNotice(@PathVariable Long noticeId) {
        noticeService.deleteNotice(noticeId);
        return ApiResponse.onSuccess(null);
    }
}
