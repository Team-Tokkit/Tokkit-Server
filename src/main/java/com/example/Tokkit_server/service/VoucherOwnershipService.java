package com.example.Tokkit_server.service;

import com.example.Tokkit_server.domain.User;
import com.example.Tokkit_server.domain.VoucherOwnership;
import com.example.Tokkit_server.dto.voucher.VoucherOwnershipResponseDto;
import com.example.Tokkit_server.repository.UserRepository;
import com.example.Tokkit_server.repository.VoucherOwnershipRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VoucherOwnershipService {

    private final VoucherOwnershipRepository voucherOwnershipRepository;
    private final UserRepository userRepository;

    // 내 바우처 전체 조회 + 검색 + 정렬
    public Page<VoucherOwnershipResponseDto> getMyVouchers(Long userId, String keyword, String sortBy, String sortOrder, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<VoucherOwnership> ownershipPage = voucherOwnershipRepository
                .findByUserAndVoucher_NameContainingIgnoreCaseOrUserAndVoucher_DescriptionContainingIgnoreCase(
                        user, keyword, user, keyword, pageRequest
                );

        return ownershipPage.map(VoucherOwnershipResponseDto::from);
    }

    // 내 바우처 상세 조회
    public VoucherOwnershipResponseDto findMyVoucherDetail(Long userId, Long voucherOwnershipId) {
        VoucherOwnership ownership = voucherOwnershipRepository.findByIdAndUserId(voucherOwnershipId, userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 소유하지 않은 바우처입니다."));
        return VoucherOwnershipResponseDto.from(ownership);
    }

    // 바우처 신청 철회
    @Transactional
    public void cancelVoucher(Long userId, Long voucherOwnershipId) {
        VoucherOwnership ownership = voucherOwnershipRepository.findByIdAndUserId(voucherOwnershipId, userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 소유하지 않은 바우처입니다."));

        switch (ownership.getStatus()) {
            case WAITING:
                voucherOwnershipRepository.delete(ownership);
                break;

            case AVAILABLE:
                Long field = ownership.getField();
                Long totalSupportAmount = ownership.getVoucher().getTotalSupportAmount().longValue();

                double remainingRate = (field.doubleValue() / totalSupportAmount.doubleValue()) * 100.0;

                if (remainingRate >= 80.0) {
                    voucherOwnershipRepository.delete(ownership);
                } else {
                    throw new IllegalArgumentException("바우처 금액의 20% 이하만 사용한 경우에만 환불 가능합니다.");
                }
                break;

            case EXPIRED:
                throw new IllegalArgumentException("만료된 바우처는 환불할 수 없습니다.");

            default:
                throw new IllegalStateException("잘못된 바우처 상태입니다.");
        }
    }

}
