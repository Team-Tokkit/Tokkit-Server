package com.example.Tokkit_server.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VerifyIdentityRequest {
    private String name;
    private String rrnPrefix;
    private String issuedDate;
}
