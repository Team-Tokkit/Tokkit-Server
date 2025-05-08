package com.example.Tokkit_server.user.utils;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@ConfigurationProperties(prefix = "naver.ocr")
public class OcrProperties {

    private final OcrEndpoint idcard = new OcrEndpoint();
    private final OcrEndpoint business = new OcrEndpoint();

    @Getter
    public static class OcrEndpoint {
        private String secretKey;
        private String invokeUrl;

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public void setInvokeUrl(String invokeUrl) {
            this.invokeUrl = invokeUrl;
        }
    }
}
