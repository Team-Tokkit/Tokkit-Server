package com.example.Tokkit_server.user.dto.request;

import lombok.Getter;

@Getter
public class PasswordVerifyRequestDto {
    private Long userId;
    private String simplePassword;
}