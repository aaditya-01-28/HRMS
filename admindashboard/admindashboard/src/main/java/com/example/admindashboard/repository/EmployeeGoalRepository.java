package com.example.admindashboard.repository;

import com.example.admindashboard.model.EmployeeGoal;
import com.example.admindashboard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository
public interface EmployeeGoalRepository extends JpaRepository<EmployeeGoal, Long> {
    List<EmployeeGoal> findByEmployee(User employee);
    
    @Query("SELECT AVG(g.completionPercentage) FROM EmployeeGoal g")
    Double getAverageCompletionPercentage();
}
