package com.example.Tokkit_server.global.repository;

import com.example.Tokkit_server.global.entity.StoreCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreCategoryRepository extends JpaRepository<StoreCategory, Long> {
}
