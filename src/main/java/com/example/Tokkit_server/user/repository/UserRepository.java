package com.example.Tokkit_server.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Tokkit_server.user.entity.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}