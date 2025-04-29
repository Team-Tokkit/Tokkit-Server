package com.example.Tokkit_server.repository;

import com.example.Tokkit_server.Enum.StoreCategory;
import com.example.Tokkit_server.domain.Store;
import com.example.Tokkit_server.domain.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {

}
