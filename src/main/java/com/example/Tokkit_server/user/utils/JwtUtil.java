package com.example.Tokkit_server.user.utils;


import com.example.Tokkit_server.merchant.auth.CustomMerchantDetails;
import com.example.Tokkit_server.user.auth.CustomUserDetails;
import com.example.Tokkit_server.user.dto.request.JwtDto;
import com.example.Tokkit_server.user.entity.Token;
import com.example.Tokkit_server.user.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.time.Instant;
import java.util.Date;
import java.util.stream.Collectors;


@Slf4j
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final Long accessExpMs;
    private final Long refreshExpMs;
    private final TokenRepository tokenRepository;

    public JwtUtil(
            @Value("${spring.jwt.secret}") String secret,
            @Value("${spring.jwt.token.access-expiration-time}") Long access,
            @Value("${spring.jwt.token.refresh-expiration-time}") Long refresh,
            TokenRepository tokenRepo
    ) {

        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm());
        accessExpMs = access;
        refreshExpMs = refresh;
        tokenRepository = tokenRepo;
    }

    // JWT 토큰을 입력으로 받아 토큰의 subject 로부터 사용자 Email 추출하는 메서드
    public String getEmail(String token) throws SignatureException {
        log.info("[ JwtUtil ] 토큰에서 이메일을 추출합니다.");
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // JWT 토큰을 입력으로 받아 토큰의 claim 에서 사용자 권한을 추출하는 메서드
    public String getRoles(String token) throws SignatureException{
        log.info("[ JwtUtil ] 토큰에서 권한을 추출합니다.");
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }

    // Token 발급하는 메서드
    public String tokenProvider(CustomUserDetails customUserDetails, Instant expiration) {

        log.info("[ JwtUtil ] 토큰을 새로 생성합니다.");
        //현재 시간
        Instant issuedAt = Instant.now();

        //토큰에 부여할 권한
        String authorities = customUserDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .header() //헤더 부분
                .add("typ", "JWT") // JWT type
                .and()
                .claim("id", customUserDetails.getId())
                .subject(customUserDetails.getUsername()) //Subject 에 username (email) 추가
                .claim("role", authorities) //권한 추가
                .issuedAt(Date.from(issuedAt)) // 현재 시간 추가
                .expiration(Date.from(expiration)) //만료 시간 추가
                .signWith(secretKey) //signature 추가
                .compact(); //합치기
    }

    public String tokenProvider(CustomMerchantDetails merchantDetails, Instant expiration) {
        String authorities = merchantDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .header().add("typ", "JWT").and()
                .subject(merchantDetails.getBusinessNumber())
                .claim("id", merchantDetails.getId())
                .claim("role", authorities)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();

    }


    // principalDetails 객체에 대해 새로운 JWT 액세스 토큰을 생성
    public String createJwtAccessToken(CustomUserDetails customUserDetails) {
        Instant expiration = Instant.now().plusMillis(accessExpMs);
        return tokenProvider(customUserDetails, expiration);
    }

    // principalDetails 객체에 대해 새로운 JWT 리프레시 토큰을 생성
    public String createJwtRefreshToken(CustomUserDetails customUserDetails) {
        Instant expiration = Instant.now().plusMillis(refreshExpMs);
        String refreshToken = tokenProvider(customUserDetails, expiration);

        // DB에 Refresh Token 저장
        tokenRepository.save(Token.builder()
                .email(customUserDetails.getUsername())
                .token(refreshToken)
                .build()
        );


        return refreshToken;
    }

    public String createJwtAccessToken(CustomMerchantDetails merchantDetails) {
        return tokenProvider(merchantDetails, Instant.now().plusMillis(accessExpMs));
    }

    public String createJwtRefreshToken(CustomMerchantDetails merchantDetails) {
        String refresh = tokenProvider(merchantDetails, Instant.now().plusMillis(refreshExpMs));
        tokenRepository.save(Token.builder()
                .email(merchantDetails.getBusinessNumber())  // subject 기준
                .token(refresh)
                .build());
        return refresh;
    }


    public Claims parseToken(String token) {
        try {
            log.info("[ JwtUtil ] 토큰을 파싱합니다.");
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("[ JwtUtil ] 만료된 토큰입니다. {}", e.getMessage());
            throw new ExpiredJwtException(null, null, "만료된 JWT 토큰입니다.");
        } catch (SecurityException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            log.error("[ JwtUtil ] 잘못된 토큰입니다. {}", e.getMessage());
            throw new SecurityException("잘못된 JWT 토큰입니다.");
        }
    }


    // 제공된 리프레시 토큰을 기반으로 JwtDto 쌍을 다시 발급
    public JwtDto reissueToken(String refreshToken) throws SignatureException {
        Claims claims = parseToken(refreshToken);

        Long id = claims.get("id", Long.class);         // id 꺼내기
        String name = claims.get("name", String.class);
        String email = claims.getSubject();              // email 꺼내기
        String role = claims.get("role", String.class);  // role 꺼내기

        CustomUserDetails userDetails = new CustomUserDetails(
                id,
                name,
                email,
                null,
                role
        );

        log.info("[ JwtUtil ] 새로운 토큰을 재발급 합니다.");

        return new JwtDto(
                createJwtAccessToken(userDetails),
                createJwtRefreshToken(userDetails)
        );
    }


    // HTTP 요청의 'Authorization' 헤더에서 JWT 액세스 토큰을 검색
    public String resolveAccessToken(HttpServletRequest request) {
        log.info("[ JwtUtil ] 헤더에서 토큰을 추출합니다.");
        String tokenFromHeader = request.getHeader("Authorization");

        if (tokenFromHeader == null || !tokenFromHeader.startsWith("Bearer ")) {
            log.warn("[ JwtUtil ] Request Header 에 토큰이 존재하지 않습니다.");
            return null;
        }

        log.info("[ JwtUtil ] 헤더에 토큰이 존재합니다.");

        return tokenFromHeader.split(" ")[1]; //Bearer 와 분리
    }

    // 리프레시 토큰의 유효성을 검사
    public void isRefreshToken(String refreshToken) throws SignatureException {
        Long id = Long.valueOf(getEmail(refreshToken));

        Token token = tokenRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Refresh Token 이 존재하지 않습니다."));

        validateToken(refreshToken);
    }

    public void validateToken(String token) {
        log.info("[ JwtUtil ] 토큰의 유효성을 검증합니다.");
        try {
            // 구문 분석 시스템의 시계가 JWT를 생성한 시스템의 시계 오차 고려
            // 약 3분 허용.
            long seconds = 3 *60;
            boolean isExpired = Jwts
                    .parser()
                    .clockSkewSeconds(seconds)
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration()
                    .before(new Date());
            if (isExpired) {
                log.info("만료된 JWT 토큰입니다.");
            }

        } catch (SecurityException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            //원하는 Exception throw
            throw new SecurityException("잘못된 토큰입니다.");
        } catch (ExpiredJwtException e) {
            //원하는 Exception throw
            throw new ExpiredJwtException(null, null, "만료된 JWT 토큰입니다.");
        }
    }
}