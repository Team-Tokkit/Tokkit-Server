package com.example.contract.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "contract_address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contract_name", nullable = false)
    private String contractName;

    @Column(name = "address", nullable = false)
    private String address;

    private String network;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
