package com.example.Tokkit_server.api_request_log.repository;

import com.example.Tokkit_server.api_request_log.entity.ApiRequestLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiRequestLogRepository extends JpaRepository<ApiRequestLog,Long> {
}
