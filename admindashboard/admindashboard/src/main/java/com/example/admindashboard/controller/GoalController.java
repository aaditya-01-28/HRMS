package com.example.admindashboard.controller;

import com.example.admindashboard.model.Goal;
import com.example.admindashboard.model.User;
import com.example.admindashboard.service.GoalService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.admindashboard.service.GoalUpdateService;
import com.example.admindashboard.service.GoalBurnChartService;
import jakarta.servlet.http.HttpSession;
import com.example.admindashboard.model.BurnChartPoint;
import java.util.List;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
@Controller
@RequestMapping("/employee/my-goals")
public class GoalController {

    @Autowired
    private GoalService goalService;
    
    @Autowired
    private GoalUpdateService goalUpdateService;
    
    @Autowired
    private GoalBurnChartService goalBurnChartService;

    @GetMapping
    public String myGoals(Model model,
                          HttpSession session) {
    	

        User user = (User) session.getAttribute("loggedInUser");
        
        System.out.println("loggedInUser      : " + session.getAttribute("loggedInUser"));
        System.out.println("employee         : " + session.getAttribute("employee"));
        System.out.println("loggedInEmployee : " + session.getAttribute("loggedInEmployee"));

        if (user == null) {
            return "redirect:/login";
        }

        List<Goal> goals = goalService.getActiveGoalsByUser(user);
        LocalDate today = LocalDate.now();

        goals.forEach(goal -> {

            int progress = goal.getCurrentProgress() == null
                    ? 0
                    : goal.getCurrentProgress();

            if (progress >= 100) {

                goal.setStatus("COMPLETED");
                return;
            }

            if (goal.getStartDate() != null
                    && goal.getTargetDate() != null
                    && !today.isBefore(goal.getStartDate())) {

                long totalDays = ChronoUnit.DAYS.between(
                        goal.getStartDate(),
                        goal.getTargetDate());

                totalDays = Math.max(totalDays, 1);

                long elapsedDays = ChronoUnit.DAYS.between(
                        goal.getStartDate(),
                        today);

                elapsedDays = Math.min(elapsedDays, totalDays);

                int expectedProgress = (int) Math.round(
                        (elapsedDays * 100.0) / totalDays);

                if (progress < expectedProgress) {

                    goal.setStatus("AT_RISK");

                } else {

                    goal.setStatus("ON_TRACK");
                }

            } else {

                goal.setStatus("ON_TRACK");
            }
        });
        int overallProgress =
                goalService.calculateOverallProgress(goals);

        model.addAttribute(
                "overallProgress",
                overallProgress);

        model.addAttribute("goals", goals);

        model.addAttribute(
                "onTrackCount",
                goals.stream()
                        .filter(g -> "ON_TRACK".equals(g.getStatus()))
                        .count());

        model.addAttribute(
                "atRiskCount",
                goals.stream()
                        .filter(g -> "AT_RISK".equals(g.getStatus()))
                        .count());

        model.addAttribute(
                "completedCount",
                goals.stream()
                        .filter(g -> "COMPLETED".equals(g.getStatus()))
                        .count());
        Goal selectedGoal =
                goals.isEmpty()
                        ? null
                        : goals.get(0);

        model.addAttribute(
                "selectedGoal",
                selectedGoal);

        model.addAttribute(
                "goalUpdates",
                selectedGoal == null
                        ? List.of()
                        : goalUpdateService.getGoalHistoryAsc(
                                selectedGoal));
        model.addAttribute(
                "completedCount",
                goals.stream()
                        .filter(g -> "COMPLETED".equals(g.getStatus()))
                        .count());
        model.addAttribute(
                "burnChart",
                goalBurnChartService.generateBurnChart(goals));
        

        return "my-goals";
    }

    @PostMapping("/create")
    public String createGoal(@ModelAttribute Goal goal,
                             HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        if (goal.getStartDate() != null && goal.getTargetDate() != null) {
            if (goal.getTargetDate().isBefore(goal.getStartDate())) {
                return "redirect:/employee/my-goals?error=" + java.net.URLEncoder.encode("End date cannot be earlier than start date.", java.nio.charset.StandardCharsets.UTF_8);
            }
        }

        goal.setUser(user);

        goal.setGoalType("SELF");

        goalService.saveGoal(goal);

        return "redirect:/employee/my-goals";
    }
    
    @GetMapping("/{goalId}/chart")
    @ResponseBody
    public List<BurnChartPoint> getGoalChart(@PathVariable Long goalId) {

        Goal goal = goalService.getGoalById(goalId);

        return goalBurnChartService.generateSingleGoalBurnChart(goal);
        
    }

    @GetMapping("/archive/{goalId}")
    public String archiveGoal(@PathVariable Long goalId) {

        goalService.archiveGoal(goalId);

        return "redirect:/employee/my-goals";
    }
}