package com.example.admindashboard.repository;

import com.example.admindashboard.model.Payslip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PayslipRepository extends JpaRepository<Payslip, Long> {
    List<Payslip> findByPayMonthAndPayYear(String month, int year);
    List<Payslip> findByUserId(Long userId);
}
