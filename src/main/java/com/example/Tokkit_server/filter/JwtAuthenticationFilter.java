package com.example.Tokkit_server.filter;

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

import com.example.Tokkit_server.auth.CustomUserDetails;
import com.example.Tokkit_server.auth.CustomUserDetailsService;
import com.example.Tokkit_server.utils.JwtUtil;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final CustomUserDetailsService customUserDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		String bearerToken = request.getHeader("Authorization");

		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			String token = bearerToken.substring(7);

			try {
				Claims claims = jwtUtil.parseToken(token);
				String email = claims.getSubject(); // subject는 이메일이니까 그대로 사용

				// 이메일로 유저 조회
				CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(email);

				// 인증 객체 생성해서 SecurityContext에 넣기
				UsernamePasswordAuthenticationToken authentication =
					new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authentication);

			} catch (Exception e) {
				logger.warn("[ JwtAuthenticationFilter ] 인증 실패: {}" + e.getMessage());
			}
		}

		filterChain.doFilter(request, response);
	}
}
