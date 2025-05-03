package com.example.Tokkit_server.global.apiPayload.code;

public interface BaseErrorCode {
	ErrorReasonDTO getReason();

	ErrorReasonDTO getReasonHttpStatus();
}
