package com.example.contract.service;

import com.example.contract.entity.ContractAddress;
import com.example.contract.repository.ContractAddressRepository;
import com.example.contract.storage.ContractAddressStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContractAddressService {
    private final ContractAddressRepository repository;
    private final ContractAddressStorage contractAddressStorage;
    /**
     * 컨트랙트 주소 저장
     */
    public void save(String contractName, String address, String network) {
        ContractAddress entity = ContractAddress.builder()
                .contractName(contractName)
                .address(address)
                .network(network)
                .build();
        repository.save(entity);

        //  메모리에도 반영
        if ("TokkitToken".equals(contractName)) {
            contractAddressStorage.setTokkitTokenAddress(address);
            log.info("📦 ContractAddressStorage 업데이트 완료: {} = {}", contractName, address);
        }
    }


    /**
     * 저장된 컨트랙트 주소중 최신 주소를 가져옴
     */
    public String getLatestAddress(String contractName) {
        return repository.findTopByContractNameOrderByCreatedAtDesc(contractName)
                .orElseThrow(() -> new IllegalStateException("Contract address not found"))
                .getAddress();
    }
}
