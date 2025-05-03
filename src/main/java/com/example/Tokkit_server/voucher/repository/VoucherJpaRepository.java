package com.example.Tokkit_server.voucher.repository;

import com.example.Tokkit_server.voucher.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherJpaRepository extends JpaRepository<Voucher, Long>, VoucherRepository {
}
