// WalletAuthService.java
package com.example.Tokkit_server.service;

import com.example.Tokkit_server.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.domain.User;
import com.example.Tokkit_server.dto.request.PasswordVerifyRequest;
import com.example.Tokkit_server.dto.response.PasswordVerifyResponse;
import com.example.Tokkit_server.repository.UserRepository;
import com.example.Tokkit_server.apiPayload.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletAuthService {

    private final UserRepository userRepository;

    public PasswordVerifyResponse verifyPassword(PasswordVerifyRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new GeneralException(ErrorStatus._BAD_REQUEST));

        boolean verified = user.getSimplePassword().equals(request.getSimplePassword());
        return new PasswordVerifyResponse(verified);
    }
}
