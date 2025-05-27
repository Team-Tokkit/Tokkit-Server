package com.example.Tokkit_server.global.config;

import com.example.Tokkit_server.api_request_log.interceptor.ApiLoggingInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final ApiLoggingInterceptor apiLoggingInterceptor;

    public WebMvcConfig(ApiLoggingInterceptor apiLoggingInterceptor) {
        this.apiLoggingInterceptor = apiLoggingInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiLoggingInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/api/swagger-ui/**",
                        "/api/v3/api-docs/**",
                        "/api/swagger-resources/**",
                        "/webjars/**"
                );
    }
}
