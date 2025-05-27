package com.example.Tokkit_server.wallet.service.query;

import com.example.Tokkit_server.global.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.global.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.wallet.dto.response.TxDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlockchainQueryService {
    private final Web3j web3j;

    public TxDetailResponse getTxHashDetail(String txHash) {
        try {
            Optional<Transaction> txOpt = web3j.ethGetTransactionByHash(txHash).send().getTransaction();
            Optional<TransactionReceipt> receiptOpt = web3j.ethGetTransactionReceipt(txHash).send().getTransactionReceipt();

            if (txOpt.isEmpty() || receiptOpt.isEmpty()) {
                throw new GeneralException(ErrorStatus.TRANSACTION_NOT_FOUND);
            }

            Transaction tx = txOpt.get();

            // 블록 조회
            EthBlock blockResponse = web3j.ethGetBlockByNumber(
                    DefaultBlockParameter.valueOf(tx.getBlockNumber()), false
            ).send();

            EthBlock.Block block = blockResponse.getBlock();

            BigInteger latestBlockNumber = web3j.ethBlockNumber().send().getBlockNumber();
            BigInteger timestamp = block.getTimestamp(); // Unix timestamp (in seconds)

            return new TxDetailResponse(tx, receiptOpt.get(), latestBlockNumber, timestamp);
        } catch (IOException e) {
            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
        }
    }

}
