package com.example.Tokkit_server.filter;

import com.example.Tokkit_server.auth.CustomUserDetails;
import com.example.Tokkit_server.domain.user.User;
import com.example.Tokkit_server.repository.UserRepository;
import com.example.Tokkit_server.utils.JwtUtil;
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
	private final UserRepository userRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		String bearerToken = request.getHeader("Authorization");

		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			String token = bearerToken.substring(7);

			try {
				Claims claims = jwtUtil.parseToken(token);
				String email = claims.getSubject();

				User user = userRepository.findByEmail(email)
					.orElseThrow(() -> new RuntimeException("User not found from token"));

				// roles가 null이면 ROLE_USER 기본 세팅
				String roles = user.getRoles();
				if (roles == null || roles.isBlank()) {
					roles = "ROLE_USER";
				}

				CustomUserDetails userDetails = new CustomUserDetails(
					user.getId(),
					user.getName(),
					user.getEmail(),
					user.getPassword(),
					roles
				);

				UsernamePasswordAuthenticationToken authentication =
					new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} catch (Exception e) {
				logger.warn("[JwtAuthenticationFilter] 인증 실패: {}" + e.getMessage());
			}
		}

		filterChain.doFilter(request, response);
	}
}
