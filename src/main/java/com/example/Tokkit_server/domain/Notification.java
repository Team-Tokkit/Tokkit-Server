package com.example.Tokkit_server.domain;

import com.example.Tokkit_server.Enum.NotificationCategory;
import com.example.Tokkit_server.domain.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Notification extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Enumerated(EnumType.STRING)
	private NotificationCategory category;

	private String title;

	private String content;

	private boolean deleted = false;

	@Column(nullable = false)
	private boolean sent = false;

	// soft delete 용 메서드
	public void softDelete() {
		this.deleted = true;
	}

	// 알림 발송 여부 확인용 메서드
	public void markAsSent() {
		this.sent = true;
	}

}
