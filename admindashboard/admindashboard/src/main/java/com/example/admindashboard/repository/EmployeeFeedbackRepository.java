package com.example.admindashboard.repository;

import com.example.admindashboard.model.EmployeeFeedback;
import com.example.admindashboard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeFeedbackRepository extends JpaRepository<EmployeeFeedback, Long> {
    List<EmployeeFeedback> findByEmployee(User employee);
    List<EmployeeFeedback> findByReviewer(User reviewer);
}
