package com.example.Tokkit_server.unified_log.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Tokkit_server.unified_log.entity.UnifiedLog;

public interface UnifiedLogRepository extends JpaRepository<UnifiedLog,Long> {
}
