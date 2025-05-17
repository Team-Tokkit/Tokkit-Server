package com.example.Tokkit_server.system_error_log.repository;

import com.example.Tokkit_server.system_error_log.entity.SystemErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemErrorLogRepository extends JpaRepository<SystemErrorLog, Long> {
}
