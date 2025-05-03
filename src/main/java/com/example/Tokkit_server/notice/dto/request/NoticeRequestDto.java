package com.example.Tokkit_server.notice.dto.request;

import com.example.Tokkit_server.notice.entity.Notice;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeRequestDto {
    private String title;
    private String content;

    public Notice to() {
        return Notice.builder()                .title(title)
                .content(content)
                .build();
    }
}
