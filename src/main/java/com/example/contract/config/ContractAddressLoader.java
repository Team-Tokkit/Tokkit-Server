package com.example.contract.config;

import java.io.File;
import java.nio.file.Paths;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Component
@Getter
@Slf4j
public class ContractAddressLoader {
    private String contractAddress;

    @PostConstruct
    public void loadContractAddress() {
        try {
            File jsonFile = Paths.get("src", "main", "resources", "contract-address.json").toFile();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonFile);
            this.contractAddress = jsonNode.get("contractAddress").asText();
            log.info(" Loaded contract address from JSON: {}", contractAddress);
        } catch (Exception e) {
            log.error(" Failed to load contract address from JSON", e);
            throw new RuntimeException(e);
        }
    }
}