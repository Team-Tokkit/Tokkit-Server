package com.example.Tokkit_server.repository;

import com.example.Tokkit_server.domain.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    Page<Notice> findAllByIsDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

}