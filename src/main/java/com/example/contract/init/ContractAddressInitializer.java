package com.example.contract.init;

import com.example.contract.service.ContractAddressService;
import com.example.contract.storage.ContractAddressStorage;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContractAddressInitializer {

    private final ContractAddressService contractAddressService;
    private final ContractAddressStorage contractAddressStorage;

    @PostConstruct
    public void init() {
        try {
            String address = contractAddressService.getLatestAddress("TokkitToken");
            contractAddressStorage.setTokkitTokenAddress(address);
            log.info("✅ 서버 시작 시 컨트랙트 주소 로드 완료: {}", address);
        } catch (Exception e) {
            log.warn("⚠️ 서버 시작 시 컨트랙트 주소를 불러오지 못했습니다: {}", e.getMessage());
        }
    }
}
