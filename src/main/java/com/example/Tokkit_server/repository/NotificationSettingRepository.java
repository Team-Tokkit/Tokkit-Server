package com.example.Tokkit_server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Tokkit_server.domain.Enum.NotificationCategory;
import com.example.Tokkit_server.domain.NotificationCategorySetting;
import com.example.Tokkit_server.domain.User;

public interface NotificationSettingRepository extends JpaRepository<NotificationCategorySetting, Long> {

	List<NotificationCategorySetting> findByUser(User user);

	// 특정 유저의 알림 카테고리 설정 가져오기
	List<NotificationCategorySetting> findByUserAndEnabledTrue(User user);

	// 유저 + 카테고리로 설정 하나 조회 (설정 수정할 때 쓸 예정)
	NotificationCategorySetting findByUserAndCategory(User user, NotificationCategory category);
}
