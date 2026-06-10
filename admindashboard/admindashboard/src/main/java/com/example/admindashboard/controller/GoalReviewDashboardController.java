package com.example.admindashboard.controller;

import com.example.admindashboard.model.User;
import com.example.admindashboard.service.GoalReviewService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GoalReviewDashboardController {

    @Autowired
    private GoalReviewService goalReviewService;

    @GetMapping("/manager/goals")
    public String managerGoals(Model model,
                               HttpSession session) {

        User manager =
                (User) session.getAttribute("loggedInUser");

        model.addAttribute(
                "reviews",
                goalReviewService.getReviewsByManager(manager)
        );

        return "manager-goals";
    }
}