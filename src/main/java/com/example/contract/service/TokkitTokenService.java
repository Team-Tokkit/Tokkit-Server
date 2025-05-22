package com.example.contract.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;

import com.example.contract.token.TokkitToken;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokkitTokenService {

    private final Web3j web3j;
    private final Credentials credentials;
    private final ContractGasProvider gasProvider;
    private final String tokkitTokenAddress;

    /**
     * 스마트 컨트랙트 객체 로드
     */
    public TokkitToken loadContract() {
        return TokkitToken.load(tokkitTokenAddress, web3j, credentials, gasProvider);
    }

    /**
     * 토큰 송금 (transfer)
     */
    public TransactionReceipt transfer(String toAddress, BigInteger amount) throws Exception {
        TokkitToken contract = loadContract();
        return contract.transfer(toAddress, amount).send();
    }

    /**
     * 토큰 민트 (mint)
     */
    public TransactionReceipt mint(String toAddress, BigInteger amount) throws Exception {
        TokkitToken contract = loadContract();
        return contract.mint(toAddress, amount).send();
    }

    /**
     * 가맹점 정산 함수 (payToMerchant)
     */
    public TransactionReceipt payToMerchant(String merchantAddress, BigInteger amount, String purpose) throws Exception {
        TokkitToken contract = loadContract();
        return contract.payToMerchant(merchantAddress, amount, purpose).send();
    }

    /**
     * 잔액 조회
     */
    public BigInteger getBalanceOf(String address) throws Exception {
        TokkitToken contract = loadContract();
        return contract.balanceOf(address).send();
    }

    /**
     * 토큰 이름
     */
    public String getName() throws Exception {
        TokkitToken contract = loadContract();
        return contract.name().send();
    }

    /**
     * 토큰 소각 (burn)
     */
    public TransactionReceipt burn(String fromAddress, BigInteger amount) throws Exception {
        TokkitToken contract = loadContract();
        return contract.burn(fromAddress, amount).send();
    }

}
