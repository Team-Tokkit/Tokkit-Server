package com.example.Tokkit_server.user.dto.request;

import lombok.Getter;

@Getter
public class EmailCodeVerificationRequestDto {
    private String email;
    private String code;
}