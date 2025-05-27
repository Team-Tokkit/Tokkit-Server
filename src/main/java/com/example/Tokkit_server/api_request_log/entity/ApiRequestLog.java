package com.example.Tokkit_server.api_request_log.entity;

import com.example.Tokkit_server.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiRequestLog extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private Long userId;

    @Column(nullable = true)
    private Long merchantId;

    @Column(nullable = false, length = 10)
    private String method;

    @Column(nullable = false, length = 255)
    private String endpoint;

    @Column(columnDefinition = "TEXT")
    private String queryParams;

    @Column(columnDefinition = "TEXT")
    private String requestBody;

    @Column(nullable = false)
    private Integer responseStatus;

    @Column(nullable = false)
    private Integer responseTimeMs;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false, length = 45)
    private String ipAddress;

    @Column(nullable = false)
    private String traceId;
}
