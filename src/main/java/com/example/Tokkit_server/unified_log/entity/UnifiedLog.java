package com.example.Tokkit_server.unified_log.entity;

import java.time.LocalDateTime;

import com.example.Tokkit_server.unified_log.dto.request.UnifiedLogSaveDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UnifiedLog {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String logType;
	private String traceId;
	private Long userId;
	private Long merchantId;
	private LocalDateTime timestamp;
	@Column(columnDefinition = "TEXT")
	private String summary;
	@Lob
	@Column(columnDefinition = "LONGTEXT")
	private String detail;
	private String statusOrSeverity;
	public static UnifiedLog fromDto(UnifiedLogSaveDto dto) {
		String detail = dto.detail();
		if (detail != null && detail.length() > 3000) {
			detail = detail.substring(0, 1000);
		}
		return UnifiedLog.builder()
			.logType(dto.logType())
			.traceId(dto.traceId())
			.userId(dto.userId())
			.merchantId(dto.merchantId())
			.timestamp(dto.timestamp())
			.summary(dto.summary())
			.detail(detail)
			.statusOrSeverity(dto.statusOrSeverity())
			.build();
	}
}
