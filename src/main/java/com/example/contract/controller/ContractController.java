package com.example.contract.controller;

import com.example.contract.dto.ContractAddressDto;
import com.example.contract.service.ContractAddressService;
import com.example.contract.service.TokkitTokenService;
import com.example.contract.storage.ContractAddressStorage;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractAddressStorage contractAddressStorage;
    private final ContractAddressService contractAddressService;
    private final TokkitTokenService tokkitTokenService;

    /**
     * Hardhat에서 스마트컨트랙트 배포 주소를 전달받고 저장
     */
    @PostMapping("/address")
    @Operation(summary = "스마트 컨트랙트 주소 등록", description = "Hardhat에서 배포된 스마트컨트랙트 주소를 전달받아 메모리 및 DB에 저장합니다.")

    public ResponseEntity<Void> receiveContractAddress(@RequestBody ContractAddressDto dto) {
        // 1. 메모리에 저장
        contractAddressStorage.setTokkitTokenAddress(dto.getTokkitToken());

        // 2. DB에도 저장
        contractAddressService.save("TokkitToken", dto.getTokkitToken(), dto.getNetwork());

        System.out.println("📥 Stored contract address: " + dto.getTokkitToken());
        return ResponseEntity.ok().build();
    }

    /**
     * 현재 메모리에 저장된 컨트랙트 주소 반환
     */
    @GetMapping("/address")
    @Operation(summary = "현재 컨트랙트 주소 조회", description = "메모리에 저장된 최신 컨트랙트 주소를 반환합니다.")

    public ResponseEntity<String> getContractAddress() {
        return ResponseEntity.ok(contractAddressStorage.getTokkitTokenAddress());
    }

    /**
     * 실제 스마트컨트랙트에서 이름 조회 테스트
     */
    @GetMapping("/name")
    @Operation(summary = "스마트 컨트랙트 이름 조회", description = "현재 연결된 TokkitToken 컨트랙트에서 name() 호출 결과를 반환합니다.")

    public ResponseEntity<String> getTokenName() throws Exception {
        return ResponseEntity.ok(tokkitTokenService.getName());
    }
}
