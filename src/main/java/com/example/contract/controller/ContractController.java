package com.example.contract.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.contract.dto.ContractAddressDto;
import com.example.contract.service.TokkitTokenService;
import com.example.contract.storage.ContractAddressStorage;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractAddressStorage contractAddressStorage;
    private final TokkitTokenService tokkitTokenService;

    @PostMapping("/address")
    public ResponseEntity<Void> receiveContractAddress(@RequestBody ContractAddressDto dto) {
        contractAddressStorage.setTokkitTokenAddress(dto.getTokkitToken());
        System.out.println("📥 Received contract address: " + dto.getTokkitToken());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/address")
    public ResponseEntity<String> getContractAddress() {
        return ResponseEntity.ok(contractAddressStorage.getTokkitTokenAddress());
    }

    @GetMapping("/name")
    public ResponseEntity<String> getTokenName() throws Exception {
        return ResponseEntity.ok(tokkitTokenService.getName());
    }

}
