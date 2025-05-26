package com.example.contract.storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class ContractAddressStorage {
    private String tokkitTokenAddress;
}
