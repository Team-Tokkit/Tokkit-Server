package com.example.Tokkit_server.notice.entity;

import com.example.Tokkit_server.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Notice extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    private boolean isDeleted;

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void setDeleted(boolean deleted) {
        this.isDeleted = deleted;
    }

}
