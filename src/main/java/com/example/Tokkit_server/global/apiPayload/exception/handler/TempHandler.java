package com.example.Tokkit_server.global.apiPayload.exception.handler;

import com.example.Tokkit_server.global.apiPayload.code.status.ErrorStatus;

public class TempHandler extends RuntimeException {
	public TempHandler(ErrorStatus errorStatus) {
		super(errorStatus.getMessage());
	}
}
