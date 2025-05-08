package com.example.Tokkit_server.merchant.service;

import com.example.Tokkit_server.merchant.repository.MerchantEmailValidationRepository;
import com.example.Tokkit_server.merchant.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MerchantService {

    private final MerchantRepository merchantRepository;
    private final MerchantEmailValidationRepository merchantEmailValidationRepository;

}
