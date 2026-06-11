package com.example.admindashboard.controller;

import com.example.admindashboard.model.Goal;
import com.example.admindashboard.model.GoalUpdate;
import com.example.admindashboard.model.User;
import com.example.admindashboard.service.GoalService;
import com.example.admindashboard.service.GoalUpdateService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/employee/my-goals")
public class GoalUpdateController {

    @Autowired
    private GoalService goalService;

    @Autowired
    private GoalUpdateService goalUpdateService;

    @PostMapping("/{goalId}/update")
    public String updateGoal(
            @PathVariable Long goalId,
            @RequestParam Integer progressPercentage,
            @RequestParam(required = false) String remarks,
            @RequestParam(required = false) MultipartFile evidence,
            HttpSession session) {

        User user =
                (User) session.getAttribute("loggedInUser");

        Goal goal = goalService.getGoalById(goalId);

        GoalUpdate update = new GoalUpdate();

        update.setGoal(goal);
        update.setUpdatedBy(user);
        update.setProgressPercentage(progressPercentage);
        update.setRemarks(remarks);

        goalUpdateService.saveUpdate(update);

        return "redirect:/employee/my-goals";
    }
}