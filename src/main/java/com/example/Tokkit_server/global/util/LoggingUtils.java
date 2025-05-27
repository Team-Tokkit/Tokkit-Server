package com.example.Tokkit_server.global.util;

import com.example.Tokkit_server.user.auth.CustomUserDetails;
import com.example.Tokkit_server.merchant.auth.CustomMerchantDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class LoggingUtils {

    public static Long getUserIdOrNull() {
        Object principal = getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails customUser) {
            return customUser.getId();
        }
        return null;
    }

    public static Long getMerchantIdOrNull() {
        Object principal = getAuthentication().getPrincipal();
        if (principal instanceof CustomMerchantDetails merchantUser) {
            return merchantUser.getId();
        }
        return null;
    }

    public static Optional<Long> getEitherUserOrMerchantId() {
        Long userId = getUserIdOrNull();
        if (userId != null) return Optional.of(userId);

        Long merchantId = getMerchantIdOrNull();
        if (merchantId != null) return Optional.of(merchantId);

        return Optional.empty();
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
