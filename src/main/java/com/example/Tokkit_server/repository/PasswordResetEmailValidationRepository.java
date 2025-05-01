package com.example.Tokkit_server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Tokkit_server.domain.user.SimplePasswordResetEmailValidation;

public interface PasswordResetEmailValidationRepository extends JpaRepository<SimplePasswordResetEmailValidation, String> {
	List<SimplePasswordResetEmailValidation> findAllByEmail(String email);
}