package com.example.Tokkit_server.global.config;

import com.example.Tokkit_server.user.filter.CustomLoginFilter;
import com.example.Tokkit_server.user.filter.JwtAuthenticationFilter;
import com.example.Tokkit_server.user.repository.UserRepository;
import com.example.Tokkit_server.user.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    private final String[] allowedUrls = {
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
            "/v3/api-docs/**",
    };

    private static final String SERVER_URL = "https://xxxxx";
    private static final String FRONT_URL = "https://xxxxx";
    private static final String FRONT_LOCALHOST_URL = "http://localhost:3000";
    private static final String SERVER_LOCALHOST_URL = "http://localhost:8080";

    @Bean
    public CorsConfigurationSource apiConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.addAllowedOrigin(SERVER_URL);
        configuration.addAllowedOrigin(FRONT_URL);
        configuration.addAllowedOrigin(FRONT_LOCALHOST_URL);
        configuration.addAllowedOrigin(SERVER_LOCALHOST_URL);

        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");

        configuration.setExposedHeaders(List.of(
                "Authorization",
                "accessToken",
                "Authorization-refresh",
                "Set-Cookie"
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(apiConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .exceptionHandling(configurer -> configurer
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.FORBIDDEN)))
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(allowedUrls).permitAll()
                        .anyRequest().authenticated());

        CustomLoginFilter loginFilter = new CustomLoginFilter(
                authenticationManager(authenticationConfiguration), jwtUtil);
        loginFilter.setFilterProcessesUrl("/api/users/login");

        http.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new JwtAuthenticationFilter(jwtUtil, userRepository), CustomLoginFilter.class);

        return http.build();
    }
}