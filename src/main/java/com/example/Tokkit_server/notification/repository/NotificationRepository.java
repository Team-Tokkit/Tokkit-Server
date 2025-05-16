package com.example.Tokkit_server.notification.repository;

import java.util.List;
import java.util.Optional;

import com.example.Tokkit_server.notification.entity.Notification;
import com.example.Tokkit_server.notification.enums.NotificationCategory;
import com.example.Tokkit_server.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 설정된 카테고리 목록으로 전체 조회
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.category IN :categories AND n.deleted = false")
    List<Notification> findByUserAndCategoriesAndDeletedFalse(@Param("user") User user, @Param("categories") List<NotificationCategory> categories);

    // 설정된 카테고리 + 특정 카테고리 조회
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.category = :category AND n.deleted = false")
    List<Notification> findByUserAndCategoryAndDeletedFalse(@Param("user") User user, @Param("category") NotificationCategory category);

    Optional<Notification> findByIdAndUser(Long id, User user);

    List<Notification> findByUserAndSentFalseAndDeletedFalse(User user);
}