// PasswordVerifyRequest.java
package com.example.Tokkit_server.dto.request;

import lombok.Getter;

@Getter
public class PasswordVerifyRequest {
    private Long userId;
    private Integer simplePassword;
}
