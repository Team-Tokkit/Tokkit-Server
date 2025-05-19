package com.example.Tokkit_server.login_log.entity;

import com.example.Tokkit_server.api_request_log.enums.Event;
import com.example.Tokkit_server.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class LoginLog extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private Long userId;

    @Column(nullable = true)
    private Long merchantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Event event;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String userAgent;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private Boolean success;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String reason;

    @Column(nullable = false)
    private String ipAddress;

    @Column(nullable = false)
    private String traceId;
}
