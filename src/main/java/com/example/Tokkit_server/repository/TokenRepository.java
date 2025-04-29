package com.example.Tokkit_server.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import com.example.Tokkit_server.domain.user.Token;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

	Optional<Token> findByEmail(String email);
}