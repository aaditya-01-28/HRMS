package com.example.admindashboard.repository;

import com.example.admindashboard.model.FinanceChecklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinanceChecklistRepository extends JpaRepository<FinanceChecklist, Long> {
    List<FinanceChecklist> findByProcessType(String processType);
}
