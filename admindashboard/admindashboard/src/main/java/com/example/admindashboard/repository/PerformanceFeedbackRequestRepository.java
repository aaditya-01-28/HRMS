package com.example.admindashboard.repository;

import com.example.admindashboard.model.PerformanceFeedbackRequest;
import com.example.admindashboard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerformanceFeedbackRequestRepository extends JpaRepository<PerformanceFeedbackRequest, Long> {
    List<PerformanceFeedbackRequest> findByEmployee(User employee);
    List<PerformanceFeedbackRequest> findByManager(User manager);
}
