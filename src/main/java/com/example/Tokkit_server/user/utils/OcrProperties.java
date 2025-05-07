package com.example.Tokkit_server.user.utils;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@ConfigurationProperties(prefix = "ocr")
public class OcrProperties {
    private String secretKey;
    private String invokeUrl;

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setInvokeUrl(String invokeUrl) {
        this.invokeUrl = invokeUrl;
    }
}