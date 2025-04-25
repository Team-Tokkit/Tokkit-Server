package com.example.Tokkit_server.domain;

import com.example.Tokkit_server.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class NotificationSetting extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;

	@Builder.Default
	private boolean notificationSystem = true;

	@Builder.Default
	private boolean notificationPayment = true;

	@Builder.Default
	private boolean notificationVoucher = true;

	@Builder.Default
	private boolean notificationToken = true;

	public void updateSettings(boolean system, boolean payment, boolean voucher, boolean token) {
		this.notificationSystem = system;
		this.notificationPayment = payment;
		this.notificationVoucher = voucher;
		this.notificationToken = token;
	}
}
