package com.example.Tokkit_server.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Tokkit_server.domain.NotificationSetting;
import com.example.Tokkit_server.domain.User;

public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {
	Optional<NotificationSetting> findByUser(User user);
}
