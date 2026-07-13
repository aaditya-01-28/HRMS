package com.example.admindashboard.repository;

import com.example.admindashboard.model.LeaveEscalation;
import com.example.admindashboard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveEscalationRepository extends JpaRepository<LeaveEscalation, Long> {
    List<LeaveEscalation> findByEscalatedTo(User user);
    List<LeaveEscalation> findByStatus(String status);
    List<LeaveEscalation> findByEscalatedToAndStatus(User user, String status);
    long countByStatus(String status);
    long countByEscalatedToAndStatus(User user, String status);
}
