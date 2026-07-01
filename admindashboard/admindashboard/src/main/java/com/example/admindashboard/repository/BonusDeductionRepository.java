package com.example.admindashboard.repository;

import com.example.admindashboard.model.BonusDeduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BonusDeductionRepository extends JpaRepository<BonusDeduction, Long> {
    List<BonusDeduction> findByEffectiveMonth(String month);
    List<BonusDeduction> findByType(String type);
    List<BonusDeduction> findByStatus(String status);
}
