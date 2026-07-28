package com.example.admindashboard.repository;

import com.example.admindashboard.model.HrCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HrCategoryRepository extends JpaRepository<HrCategory, Long> {
}
