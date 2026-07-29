package com.example.admindashboard.repository;

import com.example.admindashboard.model.EmployeeLoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeLoanRepository extends JpaRepository<EmployeeLoan, Long> {
    List<EmployeeLoan> findByStatus(String status);
}
