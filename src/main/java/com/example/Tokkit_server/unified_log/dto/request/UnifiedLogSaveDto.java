package com.example.Tokkit_server.unified_log.dto.request;

import java.time.LocalDateTime;

import com.example.Tokkit_server.api_request_log.entity.ApiRequestLog;
import com.example.Tokkit_server.login_log.entity.LoginLog;
import com.example.Tokkit_server.system_error_log.entity.SystemErrorLog;
import com.example.Tokkit_server.transaction.entity.Transaction;

public record UnifiedLogSaveDto(
	String logType,
	String traceId,
	Long userId,
	Long merchantId,
	LocalDateTime timestamp,
	String summary,

	String detail,
	String statusOrSeverity
) {

	public static UnifiedLogSaveDto fromLoginLog(LoginLog log) {
		return new UnifiedLogSaveDto(
			"LOGIN",
			log.getTraceId(),
			log.getUserId(),
			log.getMerchantId(),
			log.getTimestamp(),
			log.getEvent() + " - " + log.getIpAddress(),
			log.getUserAgent(),
			log.getSuccess() ? "SUCCESS" : "FAILURE"
		);
	}

	public static UnifiedLogSaveDto fromApiRequestLog(ApiRequestLog log) {
		return new UnifiedLogSaveDto(
			"API",
			log.getTraceId(),
			log.getUserId(),
			log.getMerchantId(),
			log.getTimestamp(),
			log.getMethod() + " " + log.getEndpoint(),
			log.getQueryParams() + "\n" + log.getRequestBody(),
			String.valueOf(log.getResponseStatus())
		);
	}

	public static UnifiedLogSaveDto fromSystemErrorLog(SystemErrorLog log) {
		return new UnifiedLogSaveDto(
			"ERROR",
			log.getTraceId(),
			log.getUserId(),
			log.getMerchantId(),
			log.getTimestamp(),
			log.getEndpoint() + " - " + log.getErrorMessage(),
			log.getStackTrace(),
			log.getSeverity().toString()
		);
	}

	public static UnifiedLogSaveDto fromTransactionLog(Transaction log) {
		return new UnifiedLogSaveDto(
			"TRANSACTION",
			log.getTraceId(),
			null,
			null,
			log.getCreatedAt(),
			log.getType() + " - " + log.getAmount(),
			log.getDescription(),
			log.getStatus().toString()
		);
	}
}
