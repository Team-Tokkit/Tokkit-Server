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

    private final ContractAddressConfig contractAddressConfig;

    @Bean
    public Web3j web3j() {
        return Web3j.build(new HttpService(contractAddressConfig.getRpcUrl()));
    }

    @Bean
    public Credentials credentials() {
        return Credentials.create(contractAddressConfig.getOwnerPrivateKey());
    }

    @Bean
    public ContractGasProvider gasProvider() {
        BigInteger gasPrice = BigInteger.valueOf(20_000_000_000L); // 20 Gwei
        BigInteger gasLimit = BigInteger.valueOf(6_700_000);
        return new StaticGasProvider(gasPrice, gasLimit);
    }

    @Bean
    public String tokkitTokenAddress() {
        return contractAddressConfig.getContractAddress();
    }
}
