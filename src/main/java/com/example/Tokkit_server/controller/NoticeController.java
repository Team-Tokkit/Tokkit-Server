package com.example.Tokkit_server.controller;

import com.example.Tokkit_server.apiPayload.ApiResponse;
import com.example.Tokkit_server.dto.notices.NoticeRequestDto;
import com.example.Tokkit_server.dto.notices.NoticeResponseDto;
import com.example.Tokkit_server.service.command.NoticeCommandService;
import com.example.Tokkit_server.service.query.NoticeQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeCommandService noticeCommandService;
    private final NoticeQueryService noticeQueryService;

    @GetMapping
    public ApiResponse<Page<NoticeResponseDto>> getNotices(
            @RequestParam(defaultValue = "0") int page
    ) {
        return ApiResponse.onSuccess(noticeQueryService.getNotices(page));
    }

    @GetMapping("/{noticeId}")
    public ApiResponse<NoticeResponseDto> getNotice(@PathVariable Long noticeId) {
        return ApiResponse.onSuccess(noticeQueryService.getNotice(noticeId));
    }

    @PostMapping
    public ApiResponse<Long> createNotice(@RequestBody NoticeRequestDto requestDto) {
        return ApiResponse.onSuccess(noticeCommandService.createNotice(requestDto));
    }

    @PutMapping("/{noticeId}")
    public ApiResponse<Void> updateNotice(@PathVariable Long noticeId, @RequestBody NoticeRequestDto requestDto) {
        noticeCommandService.updateNotice(noticeId, requestDto);
        return ApiResponse.onSuccess(null);
    }

    @PostMapping("/{noticeId}")
    public ApiResponse<Void> deleteNotice(@PathVariable Long noticeId) {
        noticeCommandService.deleteNotice(noticeId);
        return ApiResponse.onSuccess(null);
    }
}
