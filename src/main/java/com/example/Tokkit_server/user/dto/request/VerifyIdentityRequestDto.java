package com.example.Tokkit_server.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VerifyIdentityRequestDto {
    private String name;
    private String rrnPrefix;
    private String issuedDate;
}