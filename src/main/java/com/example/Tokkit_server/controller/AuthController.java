package com.example.Tokkit_server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.SignatureException;

import com.example.Tokkit_server.apiPayload.ApiResponse;
import com.example.Tokkit_server.dto.request.JwtDto;
import com.example.Tokkit_server.service.AuthService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "토큰 재발급 API입니다.")
public class AuthController {

	private final AuthService authService;

	//토큰 재발급 API
	@PostMapping("/reissue")
	@Operation(summary = "토큰 재발급 요청", description = "토큰 재발급 요청을 보내는 API입니다.")
	public ApiResponse<?> reissue(@RequestBody JwtDto jwtDto) throws SignatureException {

		log.info("[ Auth Controller ] 토큰을 재발급합니다. ");

		return ApiResponse.onSuccess(authService.reissueToken(jwtDto));
	}
}