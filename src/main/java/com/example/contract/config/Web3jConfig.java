package com.example.contract.config;

import lombok.RequiredArgsConstructor;
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

    private final ContractAddressLoader contractAddressLoader;

    @Bean
    public Web3j web3j() {
        // 로컬 Docker Hardhat 노드 주소
        return Web3j.build(new HttpService("http://localhost:8545"));
    }

    @Bean
    public Credentials credentials() {
        // Account #0의 공개된 프라이빗 키
        return Credentials.create("ac0974bec39a17e36ba4a6b4d238ff944bacb478cbed5efcae784d7bf4f2ff80");
    }

    @Bean
    public ContractGasProvider gasProvider() {
        BigInteger gasPrice = BigInteger.valueOf(20_000_000_000L); // 20 Gwei
        BigInteger gasLimit = BigInteger.valueOf(6_700_000);       // 기본 가스 한도
        return new StaticGasProvider(gasPrice, gasLimit);
    }

    @Bean
    public String tokkitTokenAddress() {
        return contractAddressLoader.getContractAddress();
    }
}
