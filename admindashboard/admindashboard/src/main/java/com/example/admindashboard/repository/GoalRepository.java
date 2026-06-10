package com.example.admindashboard.repository;

import com.example.admindashboard.model.Goal;
import com.example.admindashboard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    List<Goal> findByUser(User user);

    List<Goal> findByUserAndActiveTrue(User user);

    List<Goal> findByAssignedBy(User assignedBy);

    List<Goal> findByStatus(String status);

    List<Goal> findByUserAndStatus(User user, String status);

}