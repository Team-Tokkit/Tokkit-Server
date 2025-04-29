package com.example.Tokkit_server.service;

import com.example.Tokkit_server.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.domain.common.Notice;
import com.example.Tokkit_server.dto.notices.NoticeRequestDto;
import com.example.Tokkit_server.dto.notices.NoticeResponseDto;
import com.example.Tokkit_server.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.example.Tokkit_server.apiPayload.code.status.ErrorStatus;


@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;

    // 1. 전체 공지사항 조회
    public Page<NoticeResponseDto> getNotices(int page) {
        int size = 5;
        Page<Notice> notices = noticeRepository.findAllByIsDeletedFalseOrderByCreatedAtDesc(PageRequest.of(page, size));
        return notices.map(NoticeResponseDto::fromEntity);
    }

    // 2. 공지사항 상세 조회
    public NoticeResponseDto getNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOTICE_NOT_FOUND));

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
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOTICE_NOT_FOUND));
        notice.update(requestDto.getTitle(), requestDto.getContent());
        noticeRepository.save(notice);
    }

    // 5. 공지사항 삭제 (Admin)
    public void deleteNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOTICE_NOT_FOUND));
        notice.setDeleted(true);
        noticeRepository.save(notice);
    }

}
