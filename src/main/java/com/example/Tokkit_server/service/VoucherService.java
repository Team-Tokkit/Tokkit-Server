package com.example.Tokkit_server.service;

import com.example.Tokkit_server.domain.User;
import com.example.Tokkit_server.domain.Voucher;
import com.example.Tokkit_server.domain.VoucherOwnership;
import com.example.Tokkit_server.dto.voucher.VoucherResponseDto;
import com.example.Tokkit_server.repository.UserRepository;
import com.example.Tokkit_server.repository.VoucherOwnershipRepository;
import com.example.Tokkit_server.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class VoucherService {

    private final VoucherRepository voucherRepository;
    private final UserRepository userRepository;
    private final VoucherOwnershipRepository voucherOwnershipRepository;

    // 전체 바우처 목록 조회하기
    public List<VoucherResponseDto> findAll() {
        return voucherRepository.findAll().stream()
                .map(VoucherResponseDto::from)
                .toList();
    }

    // 바우처 필터링
    public List<VoucherResponseDto> findByCategoryAndSort(Voucher.Category category, String sortBy) {
        Stream<Voucher> voucherStream = voucherRepository.findByCategory(category).stream();

        switch (sortBy.toLowerCase()) {
            case "price_desc":
                voucherStream = voucherStream.sorted(Comparator.comparing(Voucher::getPrice).reversed());
                break;
            case "price_asc":
                voucherStream = voucherStream.sorted(Comparator.comparing(Voucher::getPrice));
                break;
            case "deadline_asc":
                voucherStream = voucherStream.sorted(Comparator.comparing(Voucher::getValidDate));
                break;
            case "deadline_desc":
                voucherStream = voucherStream.sorted(Comparator.comparing(Voucher::getValidDate).reversed());
                break;
            default:
                // 기본 정렬 - 최신순
                voucherStream = voucherStream.sorted(Comparator.comparing(Voucher::getValidDate).reversed());
                break;
        }

        return voucherStream
                .map(VoucherResponseDto::from)
                .toList();
    }

    // 바우처 검색
    public List<VoucherResponseDto> searchVouchers(String keyword) {
        return voucherRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword).stream()
                .map(VoucherResponseDto::from)
                .toList();
    }

    // 바우처 상세 조회하기
    public VoucherResponseDto findByDetails(Long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + "에 해당하는 바우처가 없습니다."));
        return VoucherResponseDto.from(voucher);
    }

    // 바우처 구매하기
    public void applyVoucher(Long userId, Long voucherId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new IllegalArgumentException("바우처를 찾을 수 없습니다."));

        VoucherOwnership ownership = VoucherOwnership.builder()
                .field((long) voucher.getSupportAmount())
                .status(VoucherOwnership.Status.WAITING)
                .account(0)
                .acquiredAt(LocalDateTime.now())
                .user(user)
                .voucher(voucher)
                .merchant(voucher.getMerchant())
                .build();

        voucherOwnershipRepository.save(ownership);
    }


}
