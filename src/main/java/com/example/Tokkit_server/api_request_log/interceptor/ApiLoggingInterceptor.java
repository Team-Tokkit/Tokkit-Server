package com.example.Tokkit_server.api_request_log.interceptor;

import com.example.Tokkit_server.api_request_log.entity.ApiRequestLog;
import com.example.Tokkit_server.api_request_log.repository.ApiRequestLogRepository;
import com.example.Tokkit_server.global.util.LoggingUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
@Slf4j
@Component
@RequiredArgsConstructor
public class ApiLoggingInterceptor implements HandlerInterceptor {

    private final ApiRequestLogRepository logRepository;

    // 요청 시작 시간 기록 (responseTime 계산용)
    private static final String START_TIME_ATTR = "apiLogStartTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME_ATTR, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {


        Long userId = LoggingUtils.getUserIdOrNull();
        if (userId == null) return;

        String traceId = MDC.get("traceId");

        String endpoint = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        if (endpoint == null) endpoint = request.getRequestURI();

        int status = response.getStatus();
        long startTime = (long) request.getAttribute(START_TIME_ATTR);
        int responseTimeMs = (int) (System.currentTimeMillis() - startTime);

        String queryParams = request.getQueryString();

        String requestBody = null;
        if (request instanceof ContentCachingRequestWrapper wrapper) {
            byte[] buf = wrapper.getContentAsByteArray();
            requestBody = new String(buf, StandardCharsets.UTF_8);
        }
        String ip = request.getRemoteAddr();
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }
        ApiRequestLog logEntity = ApiRequestLog.builder()
                .userId(userId)
                .method(request.getMethod())
                .endpoint(endpoint)
                .queryParams(queryParams)
                .requestBody(requestBody)
                .responseStatus(status)
                .responseTimeMs(responseTimeMs)
                .timestamp(LocalDateTime.now())
                .ipAddress(ip)
                .traceId(traceId)
                .build();

        logRepository.save(logEntity);

        log.info("[API LOG][{}] {} {} status={} userId={} {}ms",
                traceId, request.getMethod(), endpoint, status, userId, responseTimeMs);
    }
}
