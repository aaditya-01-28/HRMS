package com.example.admindashboard.repository;

import com.example.admindashboard.model.LearningVendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LearningVendorRepository extends JpaRepository<LearningVendor, Long> {
}
