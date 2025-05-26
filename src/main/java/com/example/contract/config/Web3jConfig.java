package com.example.contract.config;

import java.math.BigInteger;

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

    @Bean
    public Web3j web3j() {
        return Web3j.build(new HttpService("http://localhost:8545"));
    }

    @Bean
    public Credentials credentials() {
        return Credentials.create("ac0974bec39a17e36ba4a6b4d238ff944bacb478cbed5efcae784d7bf4f2ff80");
    }

    @Bean
    public ContractGasProvider gasProvider() {
        BigInteger gasPrice = BigInteger.valueOf(20_000_000_000L);
        BigInteger gasLimit = BigInteger.valueOf(6_700_000);
        return new StaticGasProvider(gasPrice, gasLimit);
    }
}
