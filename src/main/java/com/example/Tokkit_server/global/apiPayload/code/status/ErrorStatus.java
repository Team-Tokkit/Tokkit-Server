package com.example.Tokkit_server.global.apiPayload.code.status;

import org.springframework.http.HttpStatus;

import com.example.Tokkit_server.global.apiPayload.code.BaseErrorCode;
import com.example.Tokkit_server.global.apiPayload.code.ErrorReasonDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

	// For test
	TEMP_EXCEPTION(HttpStatus.BAD_REQUEST, "TEMP4001", "이거는 테스트"),

	// 가장 일반적인 응답
	_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
	_BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
	_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
	_FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),
	NOTICE_NOT_FOUND(HttpStatus.BAD_REQUEST, "NOTICE400", "공지사항을 찾을 수 없습니다."),


	// Store 관련
	STORE_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "STORE_001", "존재하지 않는 매장 카테고리입니다."),
	REGION_NOT_FOUND(HttpStatus.NOT_FOUND, "STORE_002", "존재하지 않는 지역입니다."),
	MERCHANT_NOT_FOUND(HttpStatus.NOT_FOUND, "STORE_003", "존재하지 않는 가맹점주입니다."),
	STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "STORE_004", "해당 가맹점을 찾을 수 없습니다."),

	// Wallet 관련
	USER_WALLET_NOT_FOUND(HttpStatus.NOT_FOUND, "WALLET_001", "사용자 지갑이 존재하지 않습니다."),
	MERCHANT_WALLET_NOT_FOUND(HttpStatus.NOT_FOUND, "WALLET_002", "가맹점 지갑이 존재하지 않습니다."),
	INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "WALLET_003", "토큰 잔액이 부족합니다."),

	// Voucher 관련
	VOUCHER_NOT_FOUND(HttpStatus.NOT_FOUND, "VOUCHER_001", "존재하지 않는 바우처입니다."),
	VOUCHER_ALREADY_USED(HttpStatus.BAD_REQUEST, "VOUCHER_002", "이미 사용된 바우처입니다."),
	VOUCHER_EXPIRED(HttpStatus.BAD_REQUEST, "VOUCHER_003", "만료된 바우처입니다."),
	VOUCHER_OWNERSHIP_NOT_FOUND(HttpStatus.NOT_FOUND, "VOUCHER_004", "해당 사용자의 바우처 소유 정보가 없습니다."),
	VOUCHER_SOLD_OUT(HttpStatus.BAD_REQUEST, "VOUCHER_005", "해당 바우처는 모두 소진되었습니다."),

	// User 관련
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_001", "존재하지 않는 사용자입니다."),

	// Kakao Map 관련
	INVALID_RADIUS(HttpStatus.BAD_REQUEST, "RADIUS_001", "유효하지 않은 반경입니다."),

	INVALID_LATITUDE(HttpStatus.BAD_REQUEST, "KAKAO_MAP_001", "유효하지 않은 좌표입니다.");



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
