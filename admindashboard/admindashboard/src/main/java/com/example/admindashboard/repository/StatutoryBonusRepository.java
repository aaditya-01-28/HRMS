package com.example.admindashboard.repository;

import com.example.admindashboard.model.StatutoryBonus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatutoryBonusRepository extends JpaRepository<StatutoryBonus, Long> {
}
