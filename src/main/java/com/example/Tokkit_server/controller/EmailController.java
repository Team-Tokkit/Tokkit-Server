package com.example.Tokkit_server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Tokkit_server.dto.request.EmailVerificationDto;
import com.example.Tokkit_server.service.EmailService;

@RestController
@RequestMapping("/api/users")
@Tag(name = "이메일 인증 API", description = "이메일 인증 API입니다.")
public class EmailController {

	@Autowired
	private EmailService emailService;

	@PostMapping("/emailCheck")
	public ResponseEntity<?> requestEmailValidation(@RequestParam String email) {
		try {
			emailService.sendMessage(email);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@PostMapping("/verification")
	public ResponseEntity<?> checkEmailValidation(@RequestBody EmailVerificationDto verificationDto) {
		try {
			if (emailService.ValidationCheck(verificationDto.getEmail(), verificationDto.getVerification())){
				return new ResponseEntity<>(HttpStatus.OK); //유효성 검사 통과
			} else {
				return new ResponseEntity<>(HttpStatus.ACCEPTED); //유효성 검사 실패
			}
		}catch (Exception e){
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}