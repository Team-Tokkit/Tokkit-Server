package com.example.Tokkit_server.apiPayload.exception;


import com.example.Tokkit_server.apiPayload.code.ErrorReasonDTO;
import com.example.Tokkit_server.apiPayload.code.status.ErrorStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {

    private final ErrorStatus errorStatus;

    public ErrorReasonDTO getErrorReason() {
        return this.errorStatus.getReason(); // ErrorStatus에 정의된 메시지 반환
    }

    public ErrorReasonDTO getErrorReasonHttpStatus(){
        return this.errorStatus.getReasonHttpStatus(); // HTTP 상태 코드 포함된 에러 메시지 반환
    }
}