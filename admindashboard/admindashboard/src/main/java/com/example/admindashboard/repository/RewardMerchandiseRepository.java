package com.example.admindashboard.repository;

import com.example.admindashboard.model.RewardMerchandise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RewardMerchandiseRepository extends JpaRepository<RewardMerchandise, Long> {
    List<RewardMerchandise> findByCategory(String category);
}
