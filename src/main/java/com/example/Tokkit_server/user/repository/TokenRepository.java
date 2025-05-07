package com.example.Tokkit_server.user.repository;

import com.example.Tokkit_server.user.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByEmail(String email);
}