package com.example.Tokkit_server.login_log.listener;

import com.example.Tokkit_server.login_log.enums.Event;
import com.example.Tokkit_server.login_log.entity.LoginLog;
import com.example.Tokkit_server.login_log.repository.LoginLogRepository;
import com.example.Tokkit_server.merchant.auth.CustomMerchantDetails;
import com.example.Tokkit_server.user.auth.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

import static com.example.Tokkit_server.global.util.LoggingUtils.getClientIp;
import static org.springframework.web.context.request.RequestContextHolder.currentRequestAttributes;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginLogListener {

    private final LoginLogRepository logRepository;

    private HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) currentRequestAttributes()).getRequest();
    }

    @EventListener
    public void onLoginSuccess(AuthenticationSuccessEvent event) {
        Authentication authentication = event.getAuthentication();
        Object principal = authentication.getPrincipal();

        Long userId = null;
        Long merchantId = null;

        if (principal instanceof CustomUserDetails userDetails) {
            userId = userDetails.getId();
        } else if (principal instanceof CustomMerchantDetails merchantDetails) {
            merchantId = merchantDetails.getId();
        }

        String ip = getClientIp(getCurrentRequest());
        String userAgent = getCurrentRequest().getHeader("User-Agent");
        String traceId = MDC.get("traceId");

        log.info("[LOGIN SUCCESS][traceId={}] userId={} merchantId={} ip={}", traceId, userId, merchantId, ip);

        logRepository.save(LoginLog.builder()
                .userId(userId)
                .merchantId(merchantId)
                .event(Event.LOGIN)
                .timestamp(LocalDateTime.now())
                .userAgent(userAgent)
                .success(true)
                .traceId(traceId)
                .ipAddress(ip)
                .build());
    }

    @EventListener
    public void onLoginFailure(AbstractAuthenticationFailureEvent event) {
        String ip = getClientIp(getCurrentRequest());
        String userAgent = getCurrentRequest().getHeader("User-Agent");
        String traceId = MDC.get("traceId");

        log.warn("[LOGIN FAIL][traceId={}] ip={} reason={}", traceId, ip, event.getException().getMessage());

        logRepository.save(LoginLog.builder()
                .userId(null)
                .merchantId(null)
                .event(Event.LOGIN)
                .timestamp(LocalDateTime.now())
                .userAgent(userAgent)
                .success(false)
                .reason(event.getException().getMessage())
                .traceId(traceId)
                .ipAddress(ip)
                .build());
    }
}
