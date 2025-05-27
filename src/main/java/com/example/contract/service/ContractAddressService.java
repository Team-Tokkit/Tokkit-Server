package com.example.contract.service;

import com.example.contract.entity.ContractAddress;
import com.example.contract.repository.ContractAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContractAddressService {
    private final ContractAddressRepository repository;
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
