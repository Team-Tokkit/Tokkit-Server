package com.example.Tokkit_server.repository;

import java.util.Optional;

import com.example.Tokkit_server.domain.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {
	Optional<Merchant> findByEmail(String email);
	Optional<Merchant> findByBusinessNumber(String businessNumber);

}
