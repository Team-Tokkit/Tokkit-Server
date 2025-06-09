package com.example.Tokkit_server.user.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoUpdateRequestDto {
    private String name;
    private String phoneNumber;
}