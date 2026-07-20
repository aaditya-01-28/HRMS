package com.example.admindashboard.repository;

import com.example.admindashboard.model.ItAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItAssetRepository extends JpaRepository<ItAsset, Long> {
}
