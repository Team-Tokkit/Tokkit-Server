package com.example.Tokkit_server.unified_log.service.command;

import com.example.Tokkit_server.unified_log.dto.request.UnifiedLogSaveDto;

public interface UnifiedLogCommandService {
	public void save(UnifiedLogSaveDto dto);
}
