package com.example.Tokkit_server.merchant.repository;

import com.example.Tokkit_server.merchant.entity.MerchantEmailValidation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MerchantEmailValidationRepository extends JpaRepository<MerchantEmailValidation, String> {

    List<MerchantEmailValidation> findAllByEmail(String email);
}
