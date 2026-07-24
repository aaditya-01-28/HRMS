package com.example.admindashboard.repository;

import com.example.admindashboard.model.CompanyDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyDetailsRepository extends JpaRepository<CompanyDetails, Long> {
}
