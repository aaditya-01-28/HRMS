package com.example.admindashboard.repository;

import com.example.admindashboard.model.Goal;
import com.example.admindashboard.model.GoalUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
@Repository
public interface GoalUpdateRepository extends JpaRepository<GoalUpdate, Long> {

    List<GoalUpdate> findByGoalOrderBySubmittedAtDesc(Goal goal);
    List<GoalUpdate> findByGoalOrderBySubmittedAtAsc(Goal goal);
    @Transactional
    @Modifying
    void deleteByGoal(Goal goal);

}