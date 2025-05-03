package com.example.Tokkit_server.repository;

import com.example.Tokkit_server.domain.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    // 바우처 상세 조회 (상위 5개의 사용처 조회)
    Page<Store> findByVouchersId(Long voucherId, Pageable pageable);

    @Query(value = """
    SELECT s.*
    FROM voucher_ownership vo
    JOIN voucher_store vs ON vo.voucher_id = vs.voucher_id
    JOIN store s ON vs.store_id = s.id
    WHERE vo.user_id = :userId
      AND ST_Distance_Sphere(s.location, POINT(:lng, :lat)) <= :radius
    """, nativeQuery = true)
    List<Store> findNearbyStoresByUserVoucher(
            @Param("userId") Long userId,
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radius") double radius
    );
}

}
