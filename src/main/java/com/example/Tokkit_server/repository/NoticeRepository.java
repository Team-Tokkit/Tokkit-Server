package com.example.Tokkit_server.repository;

import com.example.Tokkit_server.domain.common.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
