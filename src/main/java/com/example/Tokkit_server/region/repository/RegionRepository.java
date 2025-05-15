package com.example.Tokkit_server.region.repository;

import com.example.Tokkit_server.region.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegionRepository extends JpaRepository<Region, Long> {
    List<Region> findDistinctBySidoNameNotNull(); // 중복 제거된 시/도 이름 조회
    List<Region> findBySidoName(String sidoName); // 해당 시/도의 시군구 조회
}
