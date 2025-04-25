package com.example.Tokkit_server.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Tokkit_server.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
}