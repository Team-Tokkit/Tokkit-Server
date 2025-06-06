package com.example.Tokkit_server.unified_log.service.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Tokkit_server.unified_log.dto.request.UnifiedLogSaveDto;
import com.example.Tokkit_server.unified_log.entity.UnifiedLog;
import com.example.Tokkit_server.unified_log.repository.UnifiedLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UnifiedLogCommandServiceImpl implements UnifiedLogCommandService {

	private final UnifiedLogRepository unifiedLogRepository;

	@Override
	@Transactional
	public void save(UnifiedLogSaveDto dto) {
		UnifiedLog entity = UnifiedLog.fromDto(dto);
		unifiedLogRepository.save(entity);
	}
}
