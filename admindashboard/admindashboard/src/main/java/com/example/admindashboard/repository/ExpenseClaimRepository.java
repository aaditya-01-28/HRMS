package com.example.admindashboard.repository;

import com.example.admindashboard.model.ExpenseClaim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseClaimRepository extends JpaRepository<ExpenseClaim, Long> {
}
