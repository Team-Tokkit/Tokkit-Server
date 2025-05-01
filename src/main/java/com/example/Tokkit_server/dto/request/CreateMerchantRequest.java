package com.example.Tokkit_server.dto.request;

import com.example.Tokkit_server.Enum.StoreCategory;
import com.example.Tokkit_server.domain.Merchant;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
public class CreateMerchantRequest {

    private String name;
    private String email;
    private String phoneNumber;
    private String password;
    private String simplePassword;
    private String businessNumber;

    private String storeName;
    private String storeAddress;
    protected StoreCategory storeCategory;

    public Merchant toEntity(PasswordEncoder passwordEncoder) {
        String encodedPassword = passwordEncoder.encode(password);
        String encodedSimplePassword = passwordEncoder.encode(simplePassword);

        return Merchant.builder()
                .name(name)
                .email(email)
                .phoneNumber(phoneNumber)
                .password(encodedPassword)
                .simplePassword(encodedSimplePassword)
                .businessNumber(businessNumber)
                .isDormant(false)
                .build();
    }
}
