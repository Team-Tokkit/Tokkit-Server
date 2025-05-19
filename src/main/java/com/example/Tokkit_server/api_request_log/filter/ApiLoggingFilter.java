package com.example.Tokkit_server.api_request_log.filter;

import com.example.Tokkit_server.api_request_log.entity.ApiRequestLog;
import com.example.Tokkit_server.api_request_log.repository.ApiRequestLogRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static com.example.Tokkit_server.global.util.LoggingUtils.getUserIdOrNull;

@Slf4j
@Component
public class ApiLoggingFilter extends OncePerRequestFilter {

    private ApiRequestLogRepository logRepository;

    public ApiLoggingFilter(ApiRequestLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        if (uri.startsWith("/api/swagger-ui") ||
                uri.startsWith("/api/v3/api-docs") ||
                uri.startsWith("/api/users/login") ||
                uri.startsWith("/api/users/logout") ||
                uri.startsWith("/api/merchants/login") ||
                uri.startsWith("/api/merchants/logout") ||
                uri.startsWith("/api/ocr/idCard") ||
                uri.startsWith("/api/ocr/business") ||
                uri.startsWith("/api/regions/sigugu") ||
                uri.startsWith("/api/regions/sido") ||
                uri.startsWith("/api/merchant/register") ||
                uri.startsWith("/api/users/register") ||
                uri.startsWith("/api/merchant/findPw") ||
                uri.startsWith("/api/users/findPw") ||
                uri.startsWith("/api/users/password-update") ||
                uri.startsWith("/api/merchants/verification") ||
                uri.startsWith("/api/merchants/emailCheck")||
                uri.startsWith("/api/s3/")
        ) {
            filterChain.doFilter(request, response);
            return;
        }
        if (request.getAttribute("apiLoggingAlreadyFiltered") != null) {
            filterChain.doFilter(request, response);
            return;
        }
        request.setAttribute("apiLoggingAlreadyFiltered", true);

        long start = System.currentTimeMillis();
        String traceId = MDC.get("traceId");
        String method = request.getMethod();
        String query = request.getQueryString();
        String ip = request.getRemoteAddr();

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            int status = wrappedResponse.getStatus();
            int time = (int) (System.currentTimeMillis() - start);
            String requestBody = new String(wrappedRequest.getContentAsByteArray(), StandardCharsets.UTF_8);

            Long userId = null;
            try {
                userId = getUserIdOrNull();
            } catch (Exception e) {
                log.warn("[API LOG] userId 파싱 중 오류 발생: {}", e.getMessage());
            }
            log.info("[API LOG][traceId={}] {} {}?{} status={} time={}ms ip={}",
                    traceId, method, uri, query, status, time, ip);
            logRepository.save(ApiRequestLog.builder()
                    .userId(userId)
                    .method(method)
                    .endpoint(uri)
                    .queryParams(query)
                    .requestBody(requestBody)
                    .responseStatus(status)
                    .responseTimeMs(time)
                    .ipAddress(ip)
                    .traceId(traceId)
                    .timestamp(LocalDateTime.now())
                    .build());

            wrappedResponse.copyBodyToResponse();
        }
    }
}
