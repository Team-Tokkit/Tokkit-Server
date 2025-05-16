package com.example.Tokkit_server.user.dto.response;

import com.example.Tokkit_server.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class UserWalletResponseDto {
    public Long id;
    public String name;
    public String accountNumber;
    public Long tokenBalance;

    public static UserWalletResponseDto from(User user) {
        return UserWalletResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .accountNumber(user.getWallet().getAccountNumber())
                .tokenBalance(user.getWallet().getTokenBalance())
                .build();
    }
}
