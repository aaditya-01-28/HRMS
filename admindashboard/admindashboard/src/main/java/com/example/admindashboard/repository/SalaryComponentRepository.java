package com.example.admindashboard.repository;

import com.example.admindashboard.model.SalaryComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalaryComponentRepository extends JpaRepository<SalaryComponent, Long> {
    List<SalaryComponent> findBySalaryStructureId(Long structureId);
}
