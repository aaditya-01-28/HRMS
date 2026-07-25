package com.example.admindashboard.repository;

import com.example.admindashboard.model.BranchDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchDetailsRepository extends JpaRepository<BranchDetails, Long> {
}
