package com.example.Tokkit_server.store.dto.response;

import com.example.Tokkit_server.voucher.entity.Voucher;
import com.example.Tokkit_server.voucher_ownership.entity.VoucherOwnership;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;

public record VoucherPageResponseDto(
	List<VoucherInfoDto> content,
	int currentPage,
	int pageSize,
	int totalPages,
	long totalElements,
	boolean hasNext
) {
	public static VoucherPageResponseDto from(Page<VoucherOwnership> page) {
		List<VoucherInfoDto> content = page.getContent().stream()
			.map(VoucherInfoDto::from)
			.toList();

		return new VoucherPageResponseDto(
			content,
			page.getNumber(),
			page.getSize(),
			page.getTotalPages(),
			page.getTotalElements(),
			page.hasNext()
		);
	}

	public record VoucherInfoDto(
		Long voucherId,
		String name,
		LocalDate validUntil,
		Long balance
	) {
		public static VoucherInfoDto from(VoucherOwnership ownership) {
			Voucher v = ownership.getVoucher();
			return new VoucherInfoDto(
				v.getId(),
				v.getName(),
				v.getValidDate().toLocalDate(),
				ownership.getRemainingAmount()
			);
		}
	}
}
