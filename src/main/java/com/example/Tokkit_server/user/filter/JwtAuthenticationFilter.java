package com.example.Tokkit_server.user.filter;

import com.example.Tokkit_server.user.auth.CustomUserDetails;
import com.example.Tokkit_server.user.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

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

                Long id = claims.get("id", Long.class); // JWT에 넣은 id
                String name = claims.get("name", String.class);
                String email = claims.getSubject(); // email
                String role = claims.get("role", String.class);

                CustomUserDetails userDetails = new CustomUserDetails(
                        id,
                        name,
                        email,
                        null, // password는 사용하지 않음
                        role
                );

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                logger.warn("[JwtAuthenticationFilter] 인증 실패: {}", e);
            }
        }

        filterChain.doFilter(request, response);
    }
}
