package com.example.Tokkit_server.notification.entity;

import com.example.Tokkit_server.global.entity.BaseTimeEntity;
import com.example.Tokkit_server.notification.enums.NotificationCategory;
import com.example.Tokkit_server.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Notification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private NotificationCategory category;

    private String title;

    private String content;

    private boolean deleted = false;

    @Column(nullable = false)
    private boolean sent = false;

    // soft delete 용 메서드
    public void softDelete() {
        this.deleted = true;
    }

    // 알림 발송 여부 확인용 메서드
    public void markAsSent() {
        this.sent = true;
    }

}