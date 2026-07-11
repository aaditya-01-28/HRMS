package com.example.admindashboard.repository;

import com.example.admindashboard.model.FacilityContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilityContractRepository extends JpaRepository<FacilityContract, Long> {
}
