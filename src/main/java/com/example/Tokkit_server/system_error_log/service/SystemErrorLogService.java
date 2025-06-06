package com.example.Tokkit_server.system_error_log.service;

import com.example.Tokkit_server.system_error_log.entity.SystemErrorLog;
import com.example.Tokkit_server.system_error_log.enums.Severity;
import com.example.Tokkit_server.system_error_log.repository.SystemErrorLogRepository;
import com.example.Tokkit_server.unified_log.dto.request.UnifiedLogSaveDto;
import com.example.Tokkit_server.unified_log.service.command.UnifiedLogCommandService;
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
    private final UnifiedLogCommandService unifiedLogCommandService;
    private final JwtUtil jwtUtil;

    public void logError(Exception e, HttpServletRequest request, String traceId) {
        String endpoint = Optional.ofNullable(request.getRequestURI()).orElse("UNKNOWN");
        String errorMessage = Optional.ofNullable(e.getMessage()).orElse("No message");
        String stackTrace = getStackTraceAsString(e);

        UserOrMerchant userOrMerchant = extractUserOrMerchantIdFromRequest(request);

        log.error("[SYSTEM ERROR] endpoint: {}, traceId: {}, userId: {}, merchantId: {}, message: {}",
                endpoint, traceId, userOrMerchant.userId(), userOrMerchant.merchantId(), errorMessage);

        SystemErrorLog logEntity = SystemErrorLog.builder()
                .userId(userOrMerchant.userId())
                .merchantId(userOrMerchant.merchantId())
                .endpoint(endpoint)
                .errorMessage(errorMessage)
                .stackTrace(stackTrace)
                .timestamp(LocalDateTime.now())
                .serverName("Tokkit-Server")
                .traceId(traceId)
                .severity(Severity.ERROR)
                .build();

        systemErrorLogRepository.save(logEntity);
        unifiedLogCommandService.save(UnifiedLogSaveDto.fromSystemErrorLog(logEntity));
    }

    private UserOrMerchant extractUserOrMerchantIdFromRequest(HttpServletRequest request) {
        try {
            String token = jwtUtil.resolveAccessToken(request);
            if (token != null && jwtUtil.isTokenValid(token)) {
                if (jwtUtil.isUserToken(token)) {
                    return new UserOrMerchant(jwtUtil.extractUserId(token), null);
                } else if (jwtUtil.isMerchantToken(token)) {
                    return new UserOrMerchant(null, jwtUtil.extractMerchantId(token));
                }
            }
        } catch (Exception ex) {
            log.warn("[SystemErrorLog] Failed to extract login info from JWT: {}", ex.getMessage());
        }
        return new UserOrMerchant(null, null);
    }

    private String getStackTraceAsString(Throwable t) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : t.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }

    private record UserOrMerchant(Long userId, Long merchantId) {}
}

