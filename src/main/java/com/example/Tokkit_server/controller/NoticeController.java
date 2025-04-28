package com.example.Tokkit_server.controller;

import com.example.Tokkit_server.dto.notices.NoticeRequestDto;
import com.example.Tokkit_server.dto.notices.NoticeResponseDto;
import com.example.Tokkit_server.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping
    public Long createNotice(@RequestBody NoticeRequestDto requestDto) {
        return noticeService.createNotice(requestDto);
    }

    @GetMapping
    public List<NoticeResponseDto> getAllNotices() {
        return noticeService.getAllNotice();
    }

    @GetMapping("/{noticeId}")
    public NoticeResponseDto getNotice(@PathVariable Long noticeId) {
        return noticeService.getNotice(noticeId);
    }

    @PutMapping("/{noticeId}")
    public void updateNotice(@PathVariable Long noticeId, @RequestBody NoticeRequestDto requestDto) {
        noticeService.updateNotice(noticeId, requestDto);
    }

    @DeleteMapping("/{noticeId}")
    public void deleteNotice(@PathVariable Long noticeId) {
        noticeService.deleteNotice(noticeId);
    }
}
