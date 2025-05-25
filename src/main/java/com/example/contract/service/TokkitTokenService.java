package com.example.contract.service;

import com.example.contract.token.TokkitToken;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokkitTokenService {

    private final Web3j web3j;
    private final Credentials credentials;
    private final ContractGasProvider gasProvider;
    private final String tokkitTokenAddress;

    /**
     * 스마트 컨트랙트 로드
     */
    public TokkitToken loadContract() {
        log.info("스마트 컨트랙트 주소: {}", tokkitTokenAddress);
        return TokkitToken.load(tokkitTokenAddress, web3j, credentials, gasProvider);
    }

    /**
     * 민트 (Mint)
     */
    public TransactionReceipt mint(String toAddress, BigInteger amount) throws Exception {
        return loadContract().mint(toAddress, amount).send();
    }

    /**
     * 송금 (Transfer)
     */
    public TransactionReceipt transfer(String toAddress, BigInteger amount) throws Exception {
        return loadContract().transfer(toAddress, amount).send();
    }

    /**
     * 잔액 조회
     */
    public BigInteger getBalanceOf(String address) throws Exception {
        return loadContract().balanceOf(address).send();
    }

    /**
     * 소각 (Burn)
     */
    public TransactionReceipt burn(String fromAddress, BigInteger amount) throws Exception {
        return loadContract().burn(fromAddress, amount).send();
    }

    /**
     * 가맹점 정산
     */
    public TransactionReceipt payToMerchant(String merchantAddress, BigInteger amount, String purpose) throws Exception {
        return loadContract().payToMerchant(merchantAddress, amount, purpose).send();
    }

    /**
     * 토큰 이름 조회
     */
    public String getName() throws Exception {
        return loadContract().name().send();
    }
}
