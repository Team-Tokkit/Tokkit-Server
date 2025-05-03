package com.example.Tokkit_server.user.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Tokkit_server.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}