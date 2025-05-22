package com.example.Tokkit_server.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import java.math.BigInteger;

@Configuration
@RequiredArgsConstructor
public class Web3jConfig {

    @Value("${tokkit.contract.rpc-url}")
    private String rpcUrl;

    @Value("${tokkit.contract.owner-private-key}")
    private String privateKey;

    @Value("${tokkit.contract.address}")
    private String contractAddress;

    private final BigInteger gasPrice = BigInteger.valueOf(20_000_000_000L); // 20 Gwei
    private final BigInteger gasLimit = BigInteger.valueOf(6_721_975L);      // 기본 값

    @Bean
    public Web3j web3j() {
        return Web3j.build(new HttpService(rpcUrl));
    }

    @Bean
    public Credentials credentials() {
        return Credentials.create(privateKey);
    }

    @Bean
    public ContractGasProvider contractGasProvider() {
        return new StaticGasProvider(gasPrice, gasLimit);
    }

    @Bean
    public String tokkitTokenAddress() {
        return contractAddress;
    }
}
