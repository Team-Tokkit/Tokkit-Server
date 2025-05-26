package com.example.contract.config;

import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class Web3jConfig {

    @Value("${tokkit.contract.rpc-url}")
    private String rpcUrl;

    @Value("${tokkit.contract.owner-private-key}")
    private String privateKey;

    @Value("${tokkit.contract.gas.price}")
    private BigInteger gasPrice;

    @Value("${tokkit.contract.gas.limit}")
    private BigInteger gasLimit;

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
}
