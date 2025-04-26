package com.example.Tokkit_server.service;

import com.example.Tokkit_server.domain.Voucher;
import com.example.Tokkit_server.dto.voucher.VoucherResponseDto;
import com.example.Tokkit_server.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VoucherService {
    private final VoucherRepository voucherRepository;

    // 전체 바우처 목록 조회하기
    public List<VoucherResponseDto> findAll() {
        return voucherRepository.findAll().stream()
                .map(VoucherResponseDto::from)
                .toList();
    }

    // 바우처 카테고리 별 필터링
    public List<VoucherResponseDto> findByCategory(Voucher.Category category) {
        return voucherRepository.findByCategory(category).stream()
                .map(VoucherResponseDto::from)
                .toList();
    }

    // 바우처 목록 상세 조회하기
    public List<Voucher> VoucherDetails(Long id) {
        return voucherRepository.findAllById(id);
    }

    // 바우처 검색
    public List<VoucherResponseDto> searchVouchers(String keyword) {
        return voucherRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword).stream()
                .map(VoucherResponseDto::from)
                .toList();
    }

    // 바우처 신청하기


    // 내가 보유중인 바우처 목록 조회


    // 내가 보유중인 바우처 상세 조회

}
