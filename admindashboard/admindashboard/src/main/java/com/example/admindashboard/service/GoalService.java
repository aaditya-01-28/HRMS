package com.example.admindashboard.service;

import com.example.admindashboard.model.Goal;
import com.example.admindashboard.model.User;
import com.example.admindashboard.repository.GoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GoalService {

    @Autowired
    private GoalRepository goalRepository;

    public Goal saveGoal(Goal goal) {

        if (goal.getCurrentProgress() == null) {
            goal.setCurrentProgress(0);
        }

        if (goal.getStatus() == null) {
            goal.setStatus("ON_TRACK");
        }

        if (goal.getActive() == null) {
            goal.setActive(true);
        }

        if (goal.getCreatedAt() == null) {
            goal.setCreatedAt(LocalDateTime.now());
        }

        goal.setUpdatedAt(LocalDateTime.now());

        return goalRepository.save(goal);
    }

    public List<Goal> getGoalsByUser(User user) {
        return goalRepository.findByUser(user);
    }

    public List<Goal> getActiveGoalsByUser(User user) {
        return goalRepository.findByUserAndActiveTrue(user);
    }

    public Goal getGoalById(Long goalId) {

        return goalRepository.findById(goalId)
                .orElseThrow(() ->
                        new RuntimeException("Goal not found"));
    }

    public void archiveGoal(Long goalId) {

        Goal goal = getGoalById(goalId);

        goal.setActive(false);
        goal.setUpdatedAt(LocalDateTime.now());

        goalRepository.save(goal);
    }
    public int calculateOverallProgress(List<Goal> goals) {

        if (goals == null || goals.isEmpty()) {
            return 0;
        }

        return (int) Math.round(
                goals.stream()
                        .mapToInt(goal -> goal.getCurrentProgress())
                        .average()
                        .orElse(0)
        );
    }
    
}