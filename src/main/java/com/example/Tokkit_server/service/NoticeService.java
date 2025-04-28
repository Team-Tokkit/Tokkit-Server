package com.example.Tokkit_server.service;

import com.example.Tokkit_server.domain.common.Notice;
import com.example.Tokkit_server.dto.notices.NoticeRequestDto;
import com.example.Tokkit_server.dto.notices.NoticeResponseDto;
import com.example.Tokkit_server.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;

    // 1. 전체 공지사항 조회
    public List<NoticeResponseDto> getAllNotice() {
        return noticeRepository.findAll().stream()
                .map(NoticeResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 2. 공지사항 상세 조회
    public NoticeResponseDto getNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다. ID: " + noticeId));
        return NoticeResponseDto.fromEntity(notice);
    }

    // 3. 공지사항 생성 (Admin)
    public Long createNotice(NoticeRequestDto requestDto) {
        Notice notice = requestDto.toEntity();
        return noticeRepository.save(notice).getNoticeId();
    }

    // 4. 공지사항 수정 (Admin)
    public void updateNotice(Long noticeId, NoticeRequestDto requestDto) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다. ID: " + noticeId));
        notice.update(requestDto.getTitle(), requestDto.getContent());
        noticeRepository.save(notice);
    }

    // 5. 공지사항 삭제 (Admin)
    public void deleteNotice(Long noticeId) {
        noticeRepository.deleteById(noticeId);
    }
}
