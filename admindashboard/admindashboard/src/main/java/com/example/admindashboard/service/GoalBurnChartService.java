package com.example.admindashboard.service;

import com.example.admindashboard.model.BurnChartPoint;
import com.example.admindashboard.model.Goal;
import com.example.admindashboard.model.GoalUpdate;
import com.example.admindashboard.repository.GoalUpdateRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoalBurnChartService {

    @Autowired
    private GoalUpdateRepository goalUpdateRepository;

    public List<BurnChartPoint> generateBurnChart(List<Goal> goals) {

        List<BurnChartPoint> chart = new ArrayList<>();

        if (goals == null || goals.isEmpty()) {
            return chart;
        }

        LocalDate earliestStart = goals.stream()
                .map(Goal::getStartDate)
                .filter(d -> d != null)
                .min(LocalDate::compareTo)
                .orElse(LocalDate.now());

        LocalDate latestEnd = goals.stream()
                .map(Goal::getTargetDate)
                .filter(d -> d != null)
                .max(LocalDate::compareTo)
                .orElse(LocalDate.now());

        long totalDays =
                ChronoUnit.DAYS.between(earliestStart, latestEnd);

        if (totalDays <= 0) {
            totalDays = 1;
        }

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd MMM");

        LocalDate currentDate = earliestStart;

        while (!currentDate.isAfter(latestEnd)) {

            double expectedSum = 0;
            double actualSum = 0;

            int activeGoals = 0;

            for (Goal goal : goals) {

                if (goal.getStartDate() == null ||
                        goal.getTargetDate() == null) {

                    continue;
                }

                if (!currentDate.isBefore(goal.getStartDate())
                        && !currentDate.isAfter(goal.getTargetDate())) {

                    activeGoals++;

                    long goalDuration =
                            ChronoUnit.DAYS.between(
                                    goal.getStartDate(),
                                    goal.getTargetDate());

                    if (goalDuration <= 0) {
                        goalDuration = 1;
                    }

                    long elapsed =
                            ChronoUnit.DAYS.between(
                                    goal.getStartDate(),
                                    currentDate);

                    double expected =
                            (elapsed * 100.0) / goalDuration;

                    expected =
                            Math.min(100, Math.max(0, expected));

                    expectedSum += expected;

                    List<GoalUpdate> updates =
                            goalUpdateRepository
                                    .findByGoalOrderBySubmittedAtAsc(goal);

                    int latestProgress = 0;

                    for (GoalUpdate update : updates) {

                        if (!update.getSubmittedAt()
                                .toLocalDate()
                                .isAfter(currentDate)) {

                            latestProgress =
                                    update.getProgressPercentage();
                        }
                    }

                    actualSum += latestProgress;
                }
            }

            int expectedAverage =
                    activeGoals == 0
                            ? 0
                            : (int) Math.round(expectedSum / activeGoals);

            int actualAverage =
                    activeGoals == 0
                            ? 0
                            : (int) Math.round(actualSum / activeGoals);

            chart.add(
                    new BurnChartPoint(
                            currentDate.format(formatter),
                            expectedAverage,
                            actualAverage
                    )
            );

            currentDate = currentDate.plusDays(5);
        }

        return chart;
    }
}