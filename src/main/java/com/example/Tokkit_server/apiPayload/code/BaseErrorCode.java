package com.example.Tokkit_server.apiPayload.code;

public interface BaseErrorCode {
	ErrorReasonDTO getReason();

	ErrorReasonDTO getReasonHttpStatus();
}
