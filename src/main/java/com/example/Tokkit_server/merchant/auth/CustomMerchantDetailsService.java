package com.example.Tokkit_server.merchant.auth;

import com.example.Tokkit_server.merchant.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomMerchantDetailsService implements UserDetailsService {

    private final MerchantRepository merchantRepository;

    @Override
    public UserDetails loadUserByUsername(String businessNumber) throws UsernameNotFoundException {

        return merchantRepository.findByBusinessNumber(businessNumber)
                .map(m -> new CustomMerchantDetails(
                        m.getId(),
                        m.getName(),
                        m.getBusinessNumber(),
                        m.getPassword(),
                        m.getRoles())
                ).orElseThrow(() -> new UsernameNotFoundException("가맹점주를 찾을 수 없습니다: " + businessNumber));
    }
}