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
        log.info("🧾 스마트 컨트랙트 주소: {}", tokkitTokenAddress);
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

    /**
     * 초기 테스트
     */
    @PostConstruct
    public void testSmartContractConnection() {
        try {
            String actualAddress = credentials.getAddress();
            String expectedAddress = "0xf39Fd6e51aad88F6F4ce6aB8827279cffFb92266"; // Hardhat #0

            log.info("🔍 Credentials 주소: {}", actualAddress);
            log.info("🔍 기대 주소: {}", expectedAddress);

            if (!actualAddress.equalsIgnoreCase(expectedAddress)) {
                log.warn("❌ 프라이빗 키가 배포자 계정과 다릅니다. 키 확인 필요.");
            }

            // 테스트 민트 및 송금
            String testAddress = "0x70997970C51812dc3A010C7d01b50e0d17dc79C8"; // Hardhat #1
            BigInteger mintAmount = BigInteger.valueOf(1000);
            mint(testAddress, mintAmount);
            log.info("✅ {} 에게 {} TKT 민트 완료", testAddress, mintAmount);

            BigInteger balance = getBalanceOf(testAddress);
            log.info("💰 {} 잔액: {}", testAddress, balance);

            BigInteger transferAmount = BigInteger.valueOf(300);
            transfer(expectedAddress, transferAmount);
            log.info("✅ {} → {} 전송 완료: {} TKT", testAddress, expectedAddress, transferAmount);

            log.info("✅ 스마트 컨트랙트 연결 및 기능 테스트 성공");

        } catch (Exception e) {
            log.error("❌ 스마트 컨트랙트 테스트 실패", e);
        }
    }
}
