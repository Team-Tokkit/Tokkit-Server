package com.example.Tokkit_server.user.filter;

import com.example.Tokkit_server.user.auth.CustomUserDetails;
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
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);

            try {
                Claims claims = jwtUtil.parseToken(token);
                log.info(String.valueOf(claims));

                Object rawId = claims.get("id");
                log.info(rawId.toString());
                Long id = (rawId instanceof Integer) ? ((Integer) rawId).longValue() : (Long) rawId;

                String name = claims.get("name", String.class);
                String email = claims.getSubject();
                String role = claims.get("role", String.class);

                log.info("✅ CustomUserDetails 생성자 전달 id: {}", id);

                CustomUserDetails userDetails = new CustomUserDetails(
                        id,
                        name,
                        email,
                        null,
                        role
                );

                log.info("✅ userDetails.getId(): {}", userDetails.getId());

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                logger.warn("[JwtAuthenticationFilter] 만료된 토큰입니다: {}", e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
                return;

            } catch (Exception e) {
                logger.warn("[JwtAuthenticationFilter] 인증 실패: {}", e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

}
