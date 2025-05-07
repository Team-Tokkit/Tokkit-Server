package com.example.Tokkit_server.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SimplePasswordVerificationRequestDto {
    private String code;
}