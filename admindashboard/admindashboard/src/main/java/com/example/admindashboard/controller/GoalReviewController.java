package com.example.admindashboard.controller;

import com.example.admindashboard.model.Goal;
import com.example.admindashboard.model.User;
import com.example.admindashboard.service.GoalReviewService;
import com.example.admindashboard.service.GoalService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/manager/goals")
public class GoalReviewController {

    @Autowired
    private GoalService goalService;

    @Autowired
    private GoalReviewService goalReviewService;

    @PostMapping("/{goalId}/approve")
    public String approveGoal(@PathVariable Long goalId,
                              @RequestParam(required = false) String comments,
                              HttpSession session) {

        User manager = (User) session.getAttribute("loggedInUser");

        Goal goal = goalService.getGoalById(goalId);

        goalReviewService.approveGoal(goal, manager, comments);

        return "redirect:/manager/goals";
    }

    @PostMapping("/{goalId}/reject")
    public String rejectGoal(@PathVariable Long goalId,
                             @RequestParam(required = false) String comments,
                             HttpSession session) {

        User manager = (User) session.getAttribute("loggedInUser");

        Goal goal = goalService.getGoalById(goalId);

        goalReviewService.rejectGoal(goal, manager, comments);

        return "redirect:/manager/goals";
    }
}