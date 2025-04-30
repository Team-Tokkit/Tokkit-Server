package com.example.Tokkit_server.dto.notice;

import com.example.Tokkit_server.domain.Notice;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeRequestDto {
    private String title;
    private String content;

    public Notice to() {
        return Notice.builder()
                .title(title)
                .content(content)
                .build();
    }
}
