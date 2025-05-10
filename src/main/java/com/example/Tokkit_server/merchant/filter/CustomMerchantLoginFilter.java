package com.example.Tokkit_server.merchant.filter;

import com.example.Tokkit_server.merchant.auth.CustomMerchantDetails;
import com.example.Tokkit_server.merchant.dto.request.MerchantLoginRequestDto;
import com.example.Tokkit_server.user.dto.request.JwtDto;
import com.example.Tokkit_server.user.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class CustomMerchantLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        ObjectMapper objectMapper = new ObjectMapper();
        MerchantLoginRequestDto requestBody;

        try {
            requestBody = objectMapper.readValue(request.getInputStream(), MerchantLoginRequestDto.class);
        } catch (IOException e) {
            throw new AuthenticationServiceException("Request Body 파싱 오류", e);
        }

        String businessNumber = requestBody.getBusinessNumber();
        String password = requestBody.getPassword();

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(businessNumber, password);

        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException, ServletException {

        CustomMerchantDetails merchantDetails = (CustomMerchantDetails) authResult.getPrincipal();

        String accessToken = jwtUtil.createJwtAccessToken(merchantDetails);
        String refreshToken = jwtUtil.createJwtRefreshToken(merchantDetails);

        JwtDto jwtDto = new JwtDto(accessToken, refreshToken);

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        new ObjectMapper().writeValue(response.getWriter(), jwtDto);

        log.info("[MerchantLoginFilter] 로그인 성공 - JWT 발급 완료");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        log.warn("[MerchantLoginFilter] 로그인 실패: {}", failed.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "가맹점주 로그인 실패");
        errorResponse.put("message", failed.getMessage());
        new ObjectMapper().writeValue(response.getWriter(), errorResponse);
    }
}
