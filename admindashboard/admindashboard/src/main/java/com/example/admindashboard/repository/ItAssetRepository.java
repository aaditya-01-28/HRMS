package com.example.admindashboard.repository;

import com.example.admindashboard.model.ItAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItAssetRepository extends JpaRepository<ItAsset, Long> {
    List<ItAsset> findByStatus(String status);
    List<ItAsset> findByAssetType(String assetType);
    List<ItAsset> findByAssignedTo(String assignedTo);
}
