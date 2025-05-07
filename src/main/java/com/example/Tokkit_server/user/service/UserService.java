package com.example.Tokkit_server.user.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.example.Tokkit_server.user.entity.User;
import com.example.Tokkit_server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User saveUser(String name, String phoneNumber, String email) {
        User user = User.builder()
                .name(name)
                .phoneNumber(phoneNumber)
                .email(email)
                .build();
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}