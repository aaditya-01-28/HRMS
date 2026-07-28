package com.example.admindashboard.repository;

import com.example.admindashboard.model.EmployeeConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeConfigRepository extends JpaRepository<EmployeeConfig, Long> {
}
