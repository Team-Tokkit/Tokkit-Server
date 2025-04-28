package com.example.Tokkit_server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
@Builder
public class SignUpResonse {
	private String accessToken;
	private WalletInfo wallet;

	@Getter
	@AllArgsConstructor(staticName = "of")
	@Builder
	public static class WalletInfo {
		private String walletAddress;
		private Long balance;
	}
}
