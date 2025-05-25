package com.example.contract.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "tokkit.contract")
public class ContractAddressConfig {
    private String ownerPrivateKey;
    private String rpcUrl;
    private String contractAddress;
}
