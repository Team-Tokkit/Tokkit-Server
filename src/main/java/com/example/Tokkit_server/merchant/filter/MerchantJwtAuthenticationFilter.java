package com.example.Tokkit_server.merchant.filter;

import com.example.Tokkit_server.merchant.auth.CustomMerchantDetails;
import com.example.Tokkit_server.merchant.entity.Merchant;
import com.example.Tokkit_server.merchant.repository.MerchantRepository;
import com.example.Tokkit_server.user.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class MerchantJwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final MerchantRepository merchantRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);

            try {
                Claims claims = jwtUtil.parseToken(token);
                Long id = claims.get("id", Long.class);
                String businessNumber = claims.getSubject();
                String email = claims.get("email", String.class);
                String role = claims.get("role", String.class);

                CustomMerchantDetails merchantDetails = new CustomMerchantDetails(
                        id,
                        businessNumber,
                        email,
                        null,
                        role
                );

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(merchantDetails, null, merchantDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                log.warn("[MerchantJwtAuthenticationFilter] 만료된 토큰입니다: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
                return;

            } catch (Exception e) {
                log.warn("[MerchantJwtAuthenticationFilter] 인증 실패: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/merchants");
    }
}
