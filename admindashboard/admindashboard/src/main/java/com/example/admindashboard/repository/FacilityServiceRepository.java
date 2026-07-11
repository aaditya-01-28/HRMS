package com.example.admindashboard.repository;

import com.example.admindashboard.model.FacilityService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FacilityServiceRepository extends JpaRepository<FacilityService, Long> {
    List<FacilityService> findByCategoryIgnoreCase(String category);
}
