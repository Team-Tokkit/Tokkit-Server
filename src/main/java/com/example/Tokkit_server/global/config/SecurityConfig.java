package com.example.Tokkit_server.global.config;

import com.example.Tokkit_server.merchant.auth.CustomMerchantDetailsService;
import com.example.Tokkit_server.merchant.filter.CustomMerchantLoginFilter;
import com.example.Tokkit_server.merchant.filter.MerchantJwtAuthenticationFilter;
import com.example.Tokkit_server.merchant.repository.MerchantRepository;
import com.example.Tokkit_server.user.auth.CustomUserDetailsService;
import com.example.Tokkit_server.user.filter.CustomLoginFilter;
import com.example.Tokkit_server.user.filter.JwtAuthenticationFilter;
import com.example.Tokkit_server.user.repository.UserRepository;
import com.example.Tokkit_server.user.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final CustomMerchantDetailsService merchantDetailsService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final MerchantRepository merchantRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        AuthenticationManager authManager = authenticationManager(http);

        // 사용자 로그인 필터
        CustomLoginFilter userLoginFilter = new CustomLoginFilter(authManager, jwtUtil);
        userLoginFilter.setFilterProcessesUrl("/api/users/login");

        // 가맹점주 로그인 필터
        CustomMerchantLoginFilter merchantLoginFilter = new CustomMerchantLoginFilter(authManager, jwtUtil);
        merchantLoginFilter.setFilterProcessesUrl("/api/merchants/login");

        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/users/ocr/verify-identity",
                                "/api/users/login",
                                "/api/users/register",
                                "/api/users/emailCheck",
                                "/api/users/verification",
                                "/api/users/findPw",
                                "/api/auth/reissue",
                                "/api/auth/**",
                                "/api/merchants/emailCheck",
                                "/api/merchants/verification",
                                "/api/merchants/register",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(configurer ->
                        configurer.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.FORBIDDEN)))
                .addFilterBefore(userLoginFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(merchantLoginFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, userRepository), CustomLoginFilter.class)
                .addFilterBefore(new MerchantJwtAuthenticationFilter(jwtUtil, merchantRepository), CustomLoginFilter.class)
                .authenticationManager(authManager)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
        builder.userDetailsService(merchantDetailsService).passwordEncoder(new BCryptPasswordEncoder());
        return builder.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
