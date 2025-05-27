package com.example.contract.repository;

import com.example.contract.entity.ContractAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContractAddressRepository extends JpaRepository<ContractAddress, Long> {
    Optional<ContractAddress> findTopByContractNameOrderByCreatedAtDesc(String contractName);
}
