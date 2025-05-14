package com.example.Tokkit_server.store.repository;

import com.example.Tokkit_server.store.dto.response.StoreResponse;
import com.example.Tokkit_server.store.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    @Query("SELECT new com.example.Tokkit_server.store.dto.response.StoreResponse(s) " +
           "FROM VoucherStore vs JOIN vs.store s " +
           "WHERE vs.voucher.id = :voucherId")
    Page<StoreResponse> findByVoucherId(@Param("voucherId") Long voucherId, Pageable pageable);


    @Query(value = """
    SELECT 
        s.id AS id,
        s.store_name AS storeName,
        s.road_address AS roadAddress,
        s.new_zipcode AS newZipcode,
        s.latitude AS latitude,
        s.longitude AS longitude,
        s.store_category AS storeCategory, 
        ST_Distance_Sphere(POINT(s.longitude, s.latitude), POINT(:lng, :lat)) AS distance
    FROM wallet w
    JOIN voucher_ownership vo ON w.id = vo.wallet_id
    JOIN voucher_store vs ON vo.voucher_id = vs.voucher_id
    JOIN store s ON vs.store_id = s.id
    WHERE w.user_id = :userId
      AND ST_Distance_Sphere(POINT(s.longitude, s.latitude), POINT(:lng, :lat)) <= :radius
      AND (:category IS NULL OR s.store_category = :category)
      AND (:keyword IS NULL OR s.store_name LIKE %:keyword% OR s.road_address LIKE %:keyword%)
    ORDER BY ST_Distance_Sphere(POINT(s.longitude, s.latitude), POINT(:lng, :lat))
    """, nativeQuery = true)
    List<Object[]> findNearbyStoresRaw(
            @Param("userId") Long userId,
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radius") double radius,
            @Param("category") String category,
            @Param("keyword") String keyword
    );


    Optional<Store> findByIdAndMerchantId(Long storeId, Long merchantId);
}
