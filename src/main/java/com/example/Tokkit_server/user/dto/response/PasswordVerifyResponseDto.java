package com.example.Tokkit_server.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PasswordVerifyResponseDto {
    private boolean verified;
}