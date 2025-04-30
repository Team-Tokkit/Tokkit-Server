package com.example.Tokkit_server.service.query;

import com.example.Tokkit_server.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.domain.Notice;
import com.example.Tokkit_server.dto.notices.NoticeResponseDto;
import com.example.Tokkit_server.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeQueryService {
    private final NoticeRepository noticeRepository;
    private final int SIZE = 5;

    public Page<NoticeResponseDto> getNotices(int page) {
        Page<Notice> notices = noticeRepository.findAllByIsDeletedFalseOrderByCreatedAtDesc(PageRequest.of(page, SIZE));
        return notices.map(NoticeResponseDto::fromEntity);
    }

    public NoticeResponseDto getNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOTICE_NOT_FOUND));

        return NoticeResponseDto.fromEntity(notice);
    }
}
