package com.example.Tokkit_server.store.repository;

import com.example.Tokkit_server.store.dto.response.KakaoMapStoreResponse;
import java.util.Optional;

import com.example.Tokkit_server.store.dto.response.StoreResponse;
import com.example.Tokkit_server.store.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    @Query("SELECT new com.example.Tokkit_server.store.dto.response.StoreResponse(s) " +
           "FROM VoucherStore vs JOIN vs.store s " +
           "WHERE vs.voucher.id = :voucherId")
    Page<StoreResponse> findByVoucherId(@Param("voucherId") Long voucherId, Pageable pageable);


    @Query(value = """
    SELECT new com.example.Tokkit_server.store.dto.response.KakaoMapStoreResponse(
           s.id, s.storeName, s.roadAddress, s.newZipcode, s.latitude, s.longitude,
           ST_Distance_Sphere(POINT(s.longitude, s.latitude), POINT(:lng, :lat))
    )
    FROM wallet w
    JOIN voucher_ownership vo ON w.id = vo.wallet_id
    JOIN voucher_store vs ON vo.voucher_id = vs.voucher_id
    JOIN store s ON vs.store_id = s.id
    WHERE w.user_id = :userId
      AND ST_Distance_Sphere(POINT(s.longitude, s.latitude), POINT(:lng, :lat)) <= :radius
      AND (:category IS NULL OR s.category = :category)
      AND (:keyword IS NULL OR s.name LIKE %:keyword% OR s.address LIKE %:keyword%)
    ORDER BY ST_Distance_Sphere(POINT(s.longitude, s.latitude), POINT(:lng, :lat))
    """, nativeQuery = true)
    List<KakaoMapStoreResponse> findNearbyStores(
            @Param("userId") Long userId,
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radius") double radius,
            @Param("category") String category,
            @Param("keyword") String keyword
    );


    Optional<Store> findByIdAndMerchantId(Long storeId, Long merchantId);
}
