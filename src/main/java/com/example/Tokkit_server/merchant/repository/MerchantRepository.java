package com.example.Tokkit_server.merchant.repository;

import java.util.Optional;

import com.example.Tokkit_server.merchant.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {
	Optional<Merchant> findByEmail(String email);
	Optional<Merchant> findByBusinessNumber(String businessNumber);
	boolean existsByEmail(String email);

	boolean existsByBusinessNumber(String businessNumber);
}
