package com.example.admindashboard.service;

import com.example.admindashboard.model.Goal;
import com.example.admindashboard.model.GoalReview;
import com.example.admindashboard.model.User;
import com.example.admindashboard.repository.GoalReviewRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GoalReviewService {

    @Autowired
    private GoalReviewRepository goalReviewRepository;

    @Autowired
    private GoalService goalService;

    public GoalReview approveGoal(Goal goal,
                                  User manager,
                                  String comments) {

        GoalReview review = new GoalReview();

        review.setGoal(goal);
        review.setManager(manager);
        review.setAction("APPROVED");
        review.setComments(comments);
        review.setReviewedAt(LocalDateTime.now());

        goal.setStatus("COMPLETED");
        goalService.saveGoal(goal);

        return goalReviewRepository.save(review);
    }

    public GoalReview rejectGoal(Goal goal,
                                 User manager,
                                 String comments) {

        GoalReview review = new GoalReview();

        review.setGoal(goal);
        review.setManager(manager);
        review.setAction("REJECTED");
        review.setComments(comments);
        review.setReviewedAt(LocalDateTime.now());

        goal.setStatus("ON_TRACK");
        goalService.saveGoal(goal);

        return goalReviewRepository.save(review);
    }

    public GoalReview getReviewByGoal(Goal goal) {

        return goalReviewRepository.findByGoal(goal);
    }

    public List<GoalReview> getReviewsByManager(User manager) {

        return goalReviewRepository.findByManager(manager);
    }
}