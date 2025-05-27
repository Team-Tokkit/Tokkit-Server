package com.example.Tokkit_server.merchant.repository;

import com.example.Tokkit_server.merchant.entity.MerchantEmailValidation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MerchantEmailValidationRepository extends JpaRepository<MerchantEmailValidation, String> {

    List<MerchantEmailValidation> findAllByEmail(String email);
    Optional<MerchantEmailValidation> findTopByEmailOrderByExpDesc(String email);
}
