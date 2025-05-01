package com.example.Tokkit_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Tokkit_server.domain.Voucher;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {

}