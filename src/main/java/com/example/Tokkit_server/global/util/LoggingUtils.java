package com.example.Tokkit_server.global.util;

import com.example.Tokkit_server.user.auth.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class LoggingUtils {




    public static Long getUserIdOrNull() {
        Object principal = getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails customUser) {
            return customUser.getId();
        }
        return null;
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
