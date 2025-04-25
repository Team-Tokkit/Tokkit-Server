package com.example.Tokkit_server.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationSettingDto {
	private boolean system;
	private boolean payment;
	private boolean voucher;
	private boolean token;
}
