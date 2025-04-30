package com.example.Tokkit_server.dto.notices;

import com.example.Tokkit_server.domain.Notice;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class NoticeResponseDto {

    private Long noticeId;
    private String title;
    private String content;
    private LocalDateTime createdAt;


    public static NoticeResponseDto fromEntity(Notice notice) {
        return new NoticeResponseDto(
                notice.getNoticeId(),
                notice.getTitle(),
                notice.getContent(),
                notice.getCreatedAt()
        );
    }
}
