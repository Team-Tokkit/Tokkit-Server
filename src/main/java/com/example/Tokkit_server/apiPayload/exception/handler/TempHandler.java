package com.example.Tokkit_server.apiPayload.exception.handler;

import com.example.Tokkit_server.apiPayload.code.status.ErrorStatus;

public class TempHandler extends RuntimeException {
	public TempHandler(ErrorStatus errorStatus) {
		super(errorStatus.getMessage());
	}
}
