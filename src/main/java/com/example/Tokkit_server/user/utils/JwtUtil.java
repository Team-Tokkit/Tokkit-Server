package com.example.Tokkit_server.user.utils;


import com.example.Tokkit_server.merchant.auth.CustomMerchantDetails;
import com.example.Tokkit_server.user.auth.CustomUserDetails;
import com.example.Tokkit_server.user.dto.request.JwtDto;
import com.example.Tokkit_server.user.entity.Token;
import com.example.Tokkit_server.user.repository.TokenRepository;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
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
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Component
public class JwtUtil {

    private final SecretKey jwsSecretKey;
    private final byte[] jweSecretKey;
    private final Long accessExpMs;
    private final Long refreshExpMs;
    private final TokenRepository tokenRepository;

    public JwtUtil(
            @Value("${spring.jwt.secret}") String jwsSecret,
            @Value(("${spring.jwt.jwe.secret}")) String jweSecret,
            @Value("${spring.jwt.token.access-expiration-time}") Long access,
            @Value("${spring.jwt.token.refresh-expiration-time}") Long refresh,
            TokenRepository tokenRepo
    ) {

        this.jwsSecretKey = new SecretKeySpec(jwsSecret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm());
        this.jweSecretKey = jweSecret.getBytes(StandardCharsets.UTF_8);
        accessExpMs = access;
        refreshExpMs = refresh;
        tokenRepository = tokenRepo;
    }

    // JWT 토큰을 입력으로 받아 토큰의 subject 로부터 사용자 Email 추출하는 메서드
    public String getEmail(String token) throws SignatureException {
        return Jwts.parser()
                .verifyWith(jwsSecretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public String getRoles(String token) {
        try {
            // 우선 JWE 방식으로 복호화 시도
            Claims claims = parseToken(token);  // JWE 해석용 함수
            return claims.get("role", String.class);
        } catch (Exception e) {
            // JWS 토큰이 들어왔을 경우 (예외적으로만 허용)
            return Jwts.parser()
                    .verifyWith(jwsSecretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get("role", String.class);
        }
    }

    // Token 발급하는 메서드
    public String tokenProvider(CustomUserDetails customUserDetails, Instant expiration) {

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
                .signWith(jwsSecretKey) //signature 추가
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
                .claim("email", merchantDetails.getUsername())
                .claim("role", authorities)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(expiration))
                .signWith(jwsSecretKey)
                .compact();

    }


    public String createJwtAccessToken(CustomUserDetails customUserDetails) {
        String authorities = customUserDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        return generateEncryptedAccessToken(
                customUserDetails.getId(),
                customUserDetails.getUsername(),
                authorities,
                accessExpMs
        );
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
        String authorities = merchantDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        return generateEncryptedAccessToken(
                merchantDetails.getId(),
                merchantDetails.getBusinessNumber(),
                authorities,
                accessExpMs
        );
    }

    public String createJwtRefreshToken(CustomMerchantDetails merchantDetails) {
        String refresh = tokenProvider(merchantDetails, Instant.now().plusMillis(refreshExpMs));
        tokenRepository.save(Token.builder()
                .email(merchantDetails.getBusinessNumber())
                .token(refresh)
                .build());
        return refresh;
    }

    private String generateEncryptedAccessToken(Long id, String subject, String role, Long expirationMillis) {
        try {
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(subject)
                    .claim("id", id)
                    .claim("role", role)
                    .issueTime(new Date())
                    .expirationTime(new Date(System.currentTimeMillis() + expirationMillis))
                    .build();

            JWEHeader header = new JWEHeader(JWEAlgorithm.DIR, EncryptionMethod.A128GCM);
            EncryptedJWT jwt = new EncryptedJWT(header, claims);
            jwt.encrypt(new DirectEncrypter(jweSecretKey));

            return jwt.serialize();
        } catch (Exception e) {
            throw new RuntimeException("JWE Access Token 생성 실패", e);
        }
    }


    // 기존 JWS -> JWE 복호화 방식으로 변경
    public Claims parseToken(String token) {
        try {
            EncryptedJWT jwt = EncryptedJWT.parse(token);
            jwt.decrypt(new DirectDecrypter(jweSecretKey));
            JWTClaimsSet jwtClaims = jwt.getJWTClaimsSet();

            Map<String, Object> claimsMap = new HashMap<>();
            claimsMap.put("sub", jwtClaims.getSubject());
            claimsMap.put("id", jwtClaims.getLongClaim("id"));
            claimsMap.put("role", jwtClaims.getStringClaim("role"));

            return Jwts.claims(claimsMap);  // 이렇게 하면 Claims로 변환 가능

        } catch (ExpiredJwtException e) {
            log.warn("[ JwtUtil ] 만료된 JWE 토큰입니다. {}", e.getMessage());
            throw new ExpiredJwtException(null, null, "만료된 JWE 토큰입니다.");
        } catch (Exception e) {
            log.error("[ JwtUtil ] 잘못된 JWE 토큰입니다. {}", e.getMessage());
            throw new SecurityException("잘못된 JWE 토큰입니다.", e);
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


        return new JwtDto(
                createJwtAccessToken(userDetails),
                createJwtRefreshToken(userDetails)
        );
    }


    // HTTP 요청의 'Authorization' 헤더에서 JWT 액세스 토큰을 검색
    public String resolveAccessToken(HttpServletRequest request) {
        String tokenFromHeader = request.getHeader("Authorization");

        if (tokenFromHeader == null || !tokenFromHeader.startsWith("Bearer ")) {
            log.warn("[ JwtUtil ] Request Header 에 토큰이 존재하지 않습니다.");
            return null;
        }


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
        try {
            long seconds = 3 *60;
            boolean isExpired = Jwts
                    .parser()
                    .clockSkewSeconds(seconds)
                    .verifyWith(jwsSecretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration()
                    .before(new Date());
            if (isExpired) {
            }

        } catch (SecurityException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {

            throw new SecurityException("잘못된 토큰입니다.");
        } catch (ExpiredJwtException e) {

            throw new ExpiredJwtException(null, null, "만료된 JWT 토큰입니다.");
        }
    }

    /**
     * JWT 토큰이 유효한지 검사하는 메서드
     * @param token
     * @return
     */
    public boolean isTokenValid(String token) {
        try {
            parseToken(token); // 유효하면 Claims 리턴
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * JWT 토큰에서 사용자 ID를 추출하는 메서드
     * @param token
     * @return id
     */
    public Long extractUserId(String token) {
        Claims claims = parseToken(token);
        return claims.get("id", Long.class);
    }

    public boolean isMerchantToken(String token) throws SignatureException {
        String role = getRoles(token);
        return role != null && role.contains("MERCHANT");
    }

    public boolean isUserToken(String token) throws SignatureException {
        String role = getRoles(token);
        return role != null && role.contains("USER");
    }

    public Long extractMerchantId(String token) {
        Claims claims = parseToken(token);
        return claims.get("id", Long.class); // 동일한 id 필드를 사용
    }
}