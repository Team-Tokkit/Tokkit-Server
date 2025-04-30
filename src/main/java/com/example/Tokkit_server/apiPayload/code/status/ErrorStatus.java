package com.example.Tokkit_server.apiPayload.code.status;

import org.springframework.http.HttpStatus;

import com.example.Tokkit_server.apiPayload.code.BaseErrorCode;
import com.example.Tokkit_server.apiPayload.code.ErrorReasonDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

	// For test
	TEMP_EXCEPTION(HttpStatus.BAD_REQUEST, "TEMP4001", "이거는 테스트"),

	// 가장 일반적인 응답
	_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
	_BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
	_UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
	_FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

	NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTIFICATION404", "해당 알림을 찾을 수 없습니다."),

	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER404", "해당 유저가 존재하지 않습니다."),
	NOTIFICATION_SETTING_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTIFICATIONSETTING404", "알림 설정 정보가 존재하지 않습니다."),

	EMAIL_NOT_SEND(HttpStatus.INTERNAL_SERVER_ERROR, "EMAILSEND500", "이메일 전송 실패, 관리자에게 문의 바랍니다."),

	VERIFY_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "VERIFY500", "이메일 인증 실패, 재시도 바랍니다."),
	EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "EMAILVERIFY400", "이메일 인증이 완료되지 않았습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public ErrorReasonDTO getReason() {
		return ErrorReasonDTO.builder()
			.message(message)
			.code(code)
			.isSuccess(false)
			.build();
	}


	@Override
	public ErrorReasonDTO getReasonHttpStatus() {
		return ErrorReasonDTO.builder()
			.message(message)
			.code(code)
			.isSuccess(false)
			.httpStatus(httpStatus)
			.build();
	}
}
