package com.example.Tokkit_server.notice.repository;

import com.example.Tokkit_server.notice.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    Page<Notice> findAllByIsDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

}