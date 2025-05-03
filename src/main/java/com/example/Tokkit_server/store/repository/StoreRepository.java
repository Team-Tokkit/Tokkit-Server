package com.example.Tokkit_server.store.repository;

import com.example.Tokkit_server.store.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    // 바우처 상세 조회 (상위 5개의 사용처 조회)
    Page<Store> findByVouchersId(Long voucherId, Pageable pageable);

}
