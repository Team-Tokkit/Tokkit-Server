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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final CustomMerchantDetailsService customMerchantDetailsService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final MerchantRepository merchantRepository;

    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(
            CustomUserDetailsService customUserDetailsService,
            CustomMerchantDetailsService customMerchantDetailsService,
            JwtUtil jwtUtil,
            UserRepository userRepository,
            MerchantRepository merchantRepository,
            @Qualifier("apiConfigurationSource") CorsConfigurationSource corsConfigurationSource
    ) {
        this.customUserDetailsService = customUserDetailsService;
        this.customMerchantDetailsService = customMerchantDetailsService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.merchantRepository = merchantRepository;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    private final String[] allowedUrls = {
            "/api/ocr/idCard",
            "/api/ocr/business",
            "/api/users/login",
            "/api/auth/reissue",
            "/api/auth/**",
            "/api/merchants/login",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    };

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain userSecurityFilterChain(HttpSecurity http) throws Exception {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        AuthenticationManager manager = new ProviderManager(List.of(provider));

        CustomLoginFilter userLoginFilter = new CustomLoginFilter(manager, jwtUtil);
        userLoginFilter.setFilterProcessesUrl("/api/users/login");

        return http
                .securityMatcher("/api/users/**", "api/ocr/**")
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(config -> config.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.FORBIDDEN)))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/ocr/idCard",
                                "/api/users/register",
                                "/api/users/emailCheck",
                                "/api/users/verification",
                                "/api/users/findPw").permitAll()
                        .requestMatchers(allowedUrls).permitAll()
                        .anyRequest().authenticated())
                .addFilterAt(userLoginFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, userRepository), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain merchantSecurityFilterChain(HttpSecurity http) throws Exception {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customMerchantDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        AuthenticationManager manager = new ProviderManager(List.of(provider));

        CustomMerchantLoginFilter merchantLoginFilter = new CustomMerchantLoginFilter(manager, jwtUtil);
        merchantLoginFilter.setFilterProcessesUrl("/api/merchants/login");

        return http
                .securityMatcher("/api/merchants/**", "/api/ocr/**")
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(config -> config.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.FORBIDDEN)))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/merchants/register", "/api/merchants/emailCheck", "/api/merchants/verification", "/api/merchants/findPw").permitAll()
                        .requestMatchers(allowedUrls).permitAll()
                        .anyRequest().authenticated())
                .addFilterAt(merchantLoginFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new MerchantJwtAuthenticationFilter(jwtUtil, merchantRepository), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
