package com.example.Tokkit_server.merchant.repository;

import com.example.Tokkit_server.merchant.entity.MerchantSimplePasswordResetEmailValidation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantPasswordResetEmailValidationRepository extends JpaRepository<MerchantSimplePasswordResetEmailValidation, String> {
}
