package com.example.Tokkit_server.global.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CorsConfig {

	public static CorsConfigurationSource apiConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		//데이터 교환이 가능한 URL 지정
		ArrayList<String> allowedOriginPatterns = new ArrayList<>();
		allowedOriginPatterns.add("http://localhost:8000");
		allowedOriginPatterns.add("http://localhost:8080");
		allowedOriginPatterns.add("http://localhost:3000");
		allowedOriginPatterns.add("http://localhost:8800");

		//허용하는 HTTP METHOD 지정
		ArrayList<String> allowedHttpMethods = new ArrayList<>();
		allowedHttpMethods.add("GET");
		allowedHttpMethods.add("POST");
		allowedHttpMethods.add("PUT");
		allowedHttpMethods.add("DELETE");
		allowedHttpMethods.add("OPTIONS");

		configuration.setAllowedOrigins(allowedOriginPatterns);
		configuration.setAllowedMethods(allowedHttpMethods);

		configuration.setAllowedHeaders(Collections.singletonList("*"));
//        configuration.setAllowedHeaders(List.of(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE));
		configuration.setAllowCredentials(true); //credential TRUE

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}
}
