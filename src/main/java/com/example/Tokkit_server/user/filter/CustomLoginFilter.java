package com.example.Tokkit_server.user.filter;

import com.example.Tokkit_server.global.apiPayload.ApiResponse;
import com.example.Tokkit_server.user.auth.CustomUserDetails;
import com.example.Tokkit_server.user.dto.request.JwtDto;
import com.example.Tokkit_server.user.dto.request.LoginRequestDto;
import com.example.Tokkit_server.user.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Authentication attemptAuthentication(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response) throws AuthenticationException {

        ObjectMapper objectMapper = new ObjectMapper();
        LoginRequestDto requestBody;
        try {
            requestBody = objectMapper.readValue(request.getInputStream(), LoginRequestDto.class);
        } catch (IOException e) {
            throw new AuthenticationServiceException("[ Login Filter ] Request Body 파싱 과정에서 오류가 발생했습니다.");
        }

        String email = requestBody.getEmail();
        String password = requestBody.getPassword();

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(email, password, null);


        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain,
            @NonNull Authentication authentication) throws IOException {

        SecurityContextHolder.getContext().setAuthentication(authentication);
        eventPublisher.publishEvent(new AuthenticationSuccessEvent(authentication));

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        JwtDto jwtDto = JwtDto.builder()
                .accessToken(jwtUtil.createJwtAccessToken(customUserDetails))
                .refreshToken(jwtUtil.createJwtRefreshToken(customUserDetails))
                .build();

        ApiResponse<JwtDto> apiResponse = ApiResponse.onSuccess(jwtDto);

        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }

    @Override
    protected void unsuccessfulAuthentication(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull AuthenticationException failed) throws IOException {


        String errorMessage;
        if (failed instanceof BadCredentialsException) {
            errorMessage = "잘못된 정보입니다.";
        } else if (failed instanceof LockedException) {
            errorMessage = "계정이 잠금 상태입니다.";
        } else if (failed instanceof DisabledException) {
            errorMessage = "계정이 비활성화 되었습니다.";
        } else if (failed instanceof UsernameNotFoundException) {
            errorMessage = "계정을 찾을 수 없습니다.";
        } else if (failed instanceof AuthenticationServiceException) {
            errorMessage = "Request Body 파싱 중 오류가 발생했습니다.";
        } else {
            errorMessage = "인증에 실패했습니다.";
        }

        eventPublisher.publishEvent(new AbstractAuthenticationFailureEvent(
                new UsernamePasswordAuthenticationToken("UNKNOWN", "*****"), failed) {});

        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorMessage));
    }
}
