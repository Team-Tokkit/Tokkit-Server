package com.example.Tokkit_server.wallet.dto.response;

import lombok.Getter;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Getter
public class TxDetailResponse {
    private final String hash;
    private final String from;
    private final String to;
    private final BigInteger value;
    private final BigInteger block;
    private final int confirmations;
    private final BigInteger gasLimit;
    private final BigInteger gasPrice;
    private final BigInteger gasUsed;
    private final BigInteger nonce;
    private final String status;
    private final String timestamp; // ← 문자열로 추가

    public TxDetailResponse(
            Transaction tx,
            TransactionReceipt receipt,
            BigInteger latestBlockNumber,
            BigInteger unixTimestamp
    ) {
        this.hash = tx.getHash();
        this.from = tx.getFrom();
        this.to = tx.getTo();
        this.value = tx.getValue();
        this.block = tx.getBlockNumber();
        this.confirmations = latestBlockNumber.subtract(tx.getBlockNumber()).intValue();
        this.gasLimit = tx.getGas();
        this.gasPrice = tx.getGasPrice();
        this.gasUsed = receipt.getGasUsed();
        this.nonce = tx.getNonce();
        this.status = receipt.getStatus();
        this.timestamp = formatUnixTime(unixTimestamp);
    }

    private String formatUnixTime(BigInteger unixTime) {
        Instant instant = Instant.ofEpochSecond(unixTime.longValue());
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.of("Asia/Seoul"))
                .format(instant);
    }
}