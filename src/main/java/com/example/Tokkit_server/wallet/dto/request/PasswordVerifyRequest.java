// PasswordVerifyRequest.java
package com.example.Tokkit_server.wallet.dto.request;

import lombok.Getter;

@Getter
public class PasswordVerifyRequest {
    private Long userId;
    private String simplePassword;
}
