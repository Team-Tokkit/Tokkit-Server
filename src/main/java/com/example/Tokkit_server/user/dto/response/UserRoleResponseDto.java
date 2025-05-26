package com.example.Tokkit_server.user.dto.response;

import com.example.Tokkit_server.user.auth.CustomUserDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class UserRoleResponseDto {
    private String roles;

    public static UserRoleResponseDto of(CustomUserDetails userDetails) {
        return UserRoleResponseDto.builder()
                .roles(userDetails.getRoles())
                .build();
    }
}
