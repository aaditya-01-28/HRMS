package com.example.admindashboard.repository;

import com.example.admindashboard.model.Goal;
import com.example.admindashboard.model.GoalReview;
import com.example.admindashboard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalReviewRepository extends JpaRepository<GoalReview, Long> {

    GoalReview findByGoal(Goal goal);

    List<GoalReview> findByManager(User manager);

}