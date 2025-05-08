package com.example.Tokkit_server.wallet.entity;

import com.example.Tokkit_server.global.entity.BaseTimeEntity;
import com.example.Tokkit_server.merchant.entity.Merchant;
import com.example.Tokkit_server.user.entity.User;
import com.example.Tokkit_server.wallet.enums.WalletType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Wallet extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "merchant_id")
	private Merchant merchant;

	@Column(nullable = false)
	private String accountNumber;

	@Column(nullable = false)
	private Long depositBalance;

	private Long tokenBalance;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private WalletType walletType;


	public void updateBalance(Long deposit, Long token) {
		this.depositBalance = deposit;
		this.tokenBalance = token;
	}

	/**
	 * Wallet의 주인이 주인일수도 있고 가맹점주일수도 있기 때문에, null 이거나 둘 다의 값이 들어가는 경우를 방지 (유효성 검증)
	 */
	@PostPersist
	@PostLoad
	private void validateWalletOwnership() {
		boolean userExists = user != null;
		boolean merchantExists = merchant != null;

		if ((userExists && merchantExists) || (!userExists && !merchantExists)) {
			throw new IllegalStateException("지갑은 사용자 또는 가맹점 중 하나에만 귀속되어야 합니다.");
		}
	}
}
