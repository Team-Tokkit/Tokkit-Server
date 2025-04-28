package com.example.Tokkit_server.domain.common;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeId;

    private String title;
    private String content;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;

    // Notice 수정용 메서드
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
