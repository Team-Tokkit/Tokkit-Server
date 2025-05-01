package com.example.Tokkit_server.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Tokkit_server.domain.user.User;

public interface UserRepository extends JpaRepository<User, Long> {
	boolean existsByEmail(String email);

	Optional<User> findByEmail(String email);
}