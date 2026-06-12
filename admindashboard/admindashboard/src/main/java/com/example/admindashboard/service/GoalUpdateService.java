package com.example.admindashboard.service;

import com.example.admindashboard.model.Goal;
import com.example.admindashboard.model.GoalUpdate;
import com.example.admindashboard.repository.GoalUpdateRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GoalUpdateService {

    @Autowired
    private GoalUpdateRepository goalUpdateRepository;

    @Autowired
    private GoalService goalService;

    public GoalUpdate saveUpdate(GoalUpdate goalUpdate) {

        goalUpdate.setSubmittedAt(LocalDateTime.now());

        Goal goal = goalUpdate.getGoal();

        goal.setCurrentProgress(
                goalUpdate.getProgressPercentage());

        if(goalUpdate.getProgressPercentage() >= 100){

            goal.setStatus("COMPLETED");

        }else{

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