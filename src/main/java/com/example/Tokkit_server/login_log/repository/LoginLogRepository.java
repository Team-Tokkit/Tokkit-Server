package com.example.Tokkit_server.login_log.repository;

import com.example.Tokkit_server.login_log.entity.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {
}
