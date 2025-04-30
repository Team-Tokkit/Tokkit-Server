package com.example.Tokkit_server.domain.user;

import java.util.Date;

import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimplePasswordResetEmailValidation {
	@Id
	private String email;

	private String code;

	private Date exp;

	private boolean isVerified;

	public void setVerified(boolean isVerified) {
		this.isVerified = isVerified;
	}
}