package com.example.Tokkit_server.user.repository;

import java.util.List;

import com.example.Tokkit_server.user.entity.SimplePasswordResetEmailValidation;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PasswordResetEmailValidationRepository extends JpaRepository<SimplePasswordResetEmailValidation, String> {
    List<SimplePasswordResetEmailValidation> findAllByEmail(String email);
}