package com.example.Tokkit_server.apiPayload.code.status;

import org.springframework.http.HttpStatus;

import com.example.Tokkit_server.apiPayload.code.BaseCode;
import com.example.Tokkit_server.apiPayload.code.ReasonDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
@AllArgsConstructor
@Getter
public enum SuccessStatus implements BaseCode {
	//일반적인 응답
	_OK(HttpStatus.OK, "COMMON200", "성공입니다"),

	EMAIL_OK(HttpStatus.OK, "EMAILSEND200", "이메일이 전송되었습니다"),
	VERIFY_OK(HttpStatus.OK, "VERIFY200", "이메일 인증이 완료되었습니다"),

	NOTIFICATION_SEND_OK(HttpStatus.OK, "NOTIFICATION_SEND200", "알림 발송이 완료되었습니다"),
	NOTIFICATION_DELETE_OK(HttpStatus.OK, "NOTIFICATION_DELETE200", "알림 삭제가 완료되었습니다");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public ReasonDTO getReason() {
		return ReasonDTO.builder()
			.message(message)
			.code(code)
			.isSuccess(true)
			.build();
	}

	@Override
	public ReasonDTO getReasonHttpStauts() {
		return ReasonDTO.builder()
			.message(message)
			.code(code)
			.isSuccess(true)
			.httpStatus(httpStatus)
			.build();
	}
}
