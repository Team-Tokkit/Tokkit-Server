package com.example.Tokkit_server.domain.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "EmailValidation")
public class EmailValidation {

	@Id
	private String email;

	private Date exp;      // 인증코드 만료 시간
	private String ePw;    // 인증코드
	private boolean isVerified; // 이메일 인증 성공 여부
}
