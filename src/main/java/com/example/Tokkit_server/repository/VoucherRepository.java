package com.example.Tokkit_server.repository;

import com.example.Tokkit_server.domain.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {

    // 바우처 상세 조회
    List<Voucher> findAllById(Long id);

    // 바우처 카테고리 별 필터링
    List<Voucher> findByCategory(Voucher.Category category);

    // 바우처 검색
    // 이름 또는 설명에 키워드 포함 (OR 조건)
    List<Voucher> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String nameKeyword, String descriptionKeyword);

}

