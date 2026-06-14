package com.example.admindashboard.service;

import com.example.admindashboard.model.Goal;
import com.example.admindashboard.model.GoalUpdate;
import com.example.admindashboard.repository.GoalUpdateRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.time.LocalDate;
@Service
public class GoalUpdateService {

    @Autowired
    private GoalUpdateRepository goalUpdateRepository;

    @Autowired
    private GoalService goalService;

    public GoalUpdate saveUpdate(GoalUpdate goalUpdate) {

        goalUpdate.setSubmittedAt(LocalDateTime.now());

        Goal goal = goalUpdate.getGoal();
        if (goal.getCurrentProgress() != null
                && goal.getCurrentProgress() >= 100) {

            throw new IllegalStateException(
                    "This goal is already completed.");
        }

        int current =
                goal.getCurrentProgress() == null
                        ? 0
                        : goal.getCurrentProgress();

        int requestedIncrement =
                goalUpdate.getProgressPercentage();

        int appliedIncrement =
                Math.min(requestedIncrement,
                         100 - current);

        int updatedProgress =
                current + appliedIncrement;

        goal.setCurrentProgress(updatedProgress);

        /*
         * Save only what was actually applied
         */
        goalUpdate.setProgressPercentage(appliedIncrement);

        if (updatedProgress >= 100) {

            goal.setStatus("COMPLETED");

        } else if (goal.getTargetDate() != null
                && LocalDate.now().isAfter(goal.getTargetDate())) {

            goal.setStatus("AT_RISK");

        } else {

            goal.setStatus("ON_TRACK");
        }

        goalService.saveGoal(goal);

        return goalUpdateRepository.save(goalUpdate);
    }

    public List<GoalUpdate> getGoalHistory(Goal goal) {

        return goalUpdateRepository
                .findByGoalOrderBySubmittedAtDesc(goal);
    }
    public List<GoalUpdate> getGoalHistoryAsc(Goal goal){

        return goalUpdateRepository
                .findByGoalOrderBySubmittedAtAsc(goal);
    }

}