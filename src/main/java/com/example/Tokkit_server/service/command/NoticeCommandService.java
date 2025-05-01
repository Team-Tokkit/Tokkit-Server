package com.example.Tokkit_server.service.command;

import com.example.Tokkit_server.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.domain.Notice;
import com.example.Tokkit_server.dto.request.NoticeRequestDto;
import com.example.Tokkit_server.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeCommandService {
    private final NoticeRepository noticeRepository;

    // 공지사항 생성
    public Long createNotice(NoticeRequestDto requestDto) {
        Notice notice = requestDto.to();
        return noticeRepository.save(notice).getId();
    }

    // 공지사항 수정
    public void updateNotice(Long noticeId, NoticeRequestDto requestDto) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOTICE_NOT_FOUND));
        notice.update(requestDto.getTitle(), requestDto.getContent());
        noticeRepository.save(notice);
    }

    // 공지사항 삭제
    public void deleteNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOTICE_NOT_FOUND));
        notice.setDeleted(true);
        noticeRepository.save(notice);
    }
}
