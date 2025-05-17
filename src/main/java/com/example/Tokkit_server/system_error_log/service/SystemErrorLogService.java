package com.example.Tokkit_server.system_error_log.service;

import com.example.Tokkit_server.system_error_log.entity.SystemErrorLog;
import com.example.Tokkit_server.system_error_log.enums.Severity;
import com.example.Tokkit_server.system_error_log.repository.SystemErrorLogRepository;
import com.example.Tokkit_server.user.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SystemErrorLogService {

    private final SystemErrorLogRepository systemErrorLogRepository;
    private final JwtUtil jwtUtil;

    public void logError(Exception e, HttpServletRequest request, String traceId) {
        String endpoint = Optional.ofNullable(request.getRequestURI()).orElse("UNKNOWN");
        String errorMessage = Optional.ofNullable(e.getMessage()).orElse("No message");
        String stackTrace = getStackTraceAsString(e);
        Long userId = extractUserIdFromRequest(request);

        log.error("[SYSTEM ERROR] endpoint: {}, traceId: {}, userId: {}, message: {}", endpoint, traceId, userId, errorMessage);

        SystemErrorLog logEntity = SystemErrorLog.builder()
                .userId(userId)
                .endpoint(endpoint)
                .errorMessage(errorMessage)
                .stackTrace(stackTrace)
                .timestamp(LocalDateTime.now())
                .serverName("Tokkit-Server")
                .traceId(traceId)
                .severity(Severity.ERROR)
                .build();

        systemErrorLogRepository.save(logEntity);
    }

    private Long extractUserIdFromRequest(HttpServletRequest request) {
        try {
            String token = jwtUtil.resolveAccessToken(request);
            if (token != null && jwtUtil.isTokenValid(token)) {
                return jwtUtil.extractUserId(token);
            }
        } catch (Exception ex) {
            log.warn("[SystemErrorLog] Failed to extract userId from JWT: {}", ex.getMessage());
        }
        return null;
    }

    private String getStackTraceAsString(Throwable t) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : t.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}
