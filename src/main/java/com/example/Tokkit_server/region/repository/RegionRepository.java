package com.example.Tokkit_server.region.repository;

import com.example.Tokkit_server.region.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionRepository extends JpaRepository<Region, Long> {
}
