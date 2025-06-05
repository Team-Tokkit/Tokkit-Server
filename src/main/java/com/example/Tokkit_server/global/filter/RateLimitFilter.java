package com.example.Tokkit_server.global.filter;

import java.io.IOException;
import java.time.Duration;
import java.util.function.Supplier;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final ProxyManager<String> proxyManager;

    public RateLimitFilter(ProxyManager<String> proxyManager) {
        this.proxyManager = proxyManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain)
        throws ServletException, IOException {

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }

        String path = request.getRequestURI();

        Supplier<BucketConfiguration> configSupplier = path.startsWith("/api/users/login") || path.startsWith("/api/merchants/login")
            ? () -> BucketConfiguration.builder()
            .addLimit(Bandwidth.classic(10, Refill.greedy(2, Duration.ofSeconds(1))))
            .build()
            : () -> BucketConfiguration.builder()
            .addLimit(Bandwidth.classic(100, Refill.greedy(10, Duration.ofSeconds(1))))
            .build();

        String key = "rate:" + ip + ":" + path;
        Bucket bucket = proxyManager.builder().build(key, configSupplier);

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(429);
            response.setContentType("text/plain");
            response.getWriter().write("Rate limit exceeded. Please try again later.");
        }
    }

}
