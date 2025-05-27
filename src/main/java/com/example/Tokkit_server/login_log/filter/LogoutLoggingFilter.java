package com.example.Tokkit_server.login_log.filter;

import com.example.Tokkit_server.login_log.enums.Event;
import com.example.Tokkit_server.login_log.entity.LoginLog;
import com.example.Tokkit_server.login_log.repository.LoginLogRepository;
import com.example.Tokkit_server.user.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.SignatureException;
import java.time.LocalDateTime;


@RequiredArgsConstructor
@Slf4j
public class LogoutLoggingFilter extends OncePerRequestFilter {

    private static final String ALREADY_LOGGED_ATTRIBUTE = "LogoutLoggingFilterAlreadyLogged";

    private final LoginLogRepository logRepository;

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (request.getAttribute(ALREADY_LOGGED_ATTRIBUTE) != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String uri = request.getRequestURI();
        boolean isLogoutRequest = uri.equals("/api/users/logout") || uri.equals("/api/merchants/logout");

        Long userId = null;
        Long merchantId = null;

        if (isLogoutRequest) {
            String token = extractToken(request);

            if (token != null && jwtUtil.isTokenValid(token)) {
                Long id = jwtUtil.extractUserId(token);
                String role = null;
                try {
                    role = jwtUtil.getRoles(token);
                } catch (SignatureException e) {
                    throw new RuntimeException(e);
                }

                if ("MERCHANT".equals(role)) {
                    merchantId = id;
                } else {
                    userId = id;
                }
            }

            String traceId = MDC.get("traceId");
            String ip = request.getRemoteAddr();
            String ua = request.getHeader("User-Agent");

            log.info("[LOGOUT] traceId={}, userId={}, merchantId={}, ip={}, ua={}",
                    traceId, userId, merchantId, ip, ua);
            logRepository.save(LoginLog.builder()
                    .userId(userId)
                    .merchantId(merchantId)
                    .success(true)
                    .event(Event.LOGOUT)
                    .traceId(traceId)
                    .ipAddress(ip)
                    .userAgent(ua)
                    .timestamp(LocalDateTime.now())
                    .build());

            request.setAttribute(ALREADY_LOGGED_ATTRIBUTE, true);
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}

