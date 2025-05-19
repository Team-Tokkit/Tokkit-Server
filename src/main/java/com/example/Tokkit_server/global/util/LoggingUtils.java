package com.example.Tokkit_server.global.util;

import com.example.Tokkit_server.global.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.global.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.user.auth.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class LoggingUtils {


    public static Long getUserId() {
        Object principal = getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails customUser) {
            return customUser.getId();
        }
        throw new IllegalStateException("User ID not found in security context");
    }

    public static Long getUserIdOrNull() {
        try {
            return getUserId();
        } catch (Exception e) {
             throw new GeneralException(ErrorStatus.USER_NOT_FOUND);
        }
    }

    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
