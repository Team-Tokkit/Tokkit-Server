package com.example.Tokkit_server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Tokkit_server.domain.Notification;
import com.example.Tokkit_server.domain.NotificationCategory;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
	// 알림 카테고리가 null이라면 전체 알림 조회(user별로 다르니까 userId로 조회)
	List<Notification> findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(Long userId);


	// 알림 카테고리가 존재하면 해당 카테고리만 조회
	List<Notification> findByUserIdAndCategoryInAndIsDeletedFalseOrderByCreatedAtDesc(Long userId, List<NotificationCategory> categories);

}
