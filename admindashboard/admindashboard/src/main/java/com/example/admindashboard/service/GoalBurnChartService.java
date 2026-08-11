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
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.TreeSet;
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

        /*****************************************
         * ACTUAL UPDATE DATES
         *****************************************/
        Set<LocalDate> chartDates = new TreeSet<>();

        chartDates.add(earliestStart);
        chartDates.add(latestEnd);

        for (Goal goal : goals) {

            List<GoalUpdate> updates =
                    goalUpdateRepository
                            .findByGoalOrderBySubmittedAtAsc(goal);

            for (GoalUpdate update : updates) {

                chartDates.add(
                        update.getSubmittedAt()
                              .toLocalDate()
                );
            }
        }

        /*****************************************
         * BUILD CHART
         *****************************************/
        for (LocalDate currentDate : chartDates) {

            double expectedSum = 0;
            double actualSum = 0;

            int activeGoals = 0;

            for (Goal goal : goals) {

                if (goal.getStartDate() == null ||
                        goal.getTargetDate() == null) {

                    continue;
                }

                if (!currentDate.isBefore(goal.getStartDate())) {

                    activeGoals++;

                    /*********************************
                     * PLANNED
                     *********************************/
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

                    elapsed = Math.min(
                            elapsed,
                            goalDuration);

                    double expected =
                            (elapsed * 100.0)
                                    / goalDuration;

                    expected =
                            Math.min(100,
                                    Math.max(0,
                                            expected));

                    expectedSum += expected;

                    /*********************************
                     * ACTUAL
                     *********************************/
                    List<GoalUpdate> updates =
                            goalUpdateRepository
                                    .findByGoalOrderBySubmittedAtAsc(goal);

                    int cumulativeProgress = 0;

                    for (GoalUpdate update : updates) {

                        if (!update.getSubmittedAt()
                                .toLocalDate()
                                .isAfter(currentDate)) {

                            cumulativeProgress +=
                                    update.getProgressPercentage();
                        }
                    }

                    cumulativeProgress =
                            Math.min(cumulativeProgress, 100);

                    actualSum += cumulativeProgress;
                  
                }
            }

            int totalGoals = goals.size();

            int expectedAverage =
                    totalGoals == 0
                            ? 0
                            : (int) Math.round(
                                    expectedSum
                                            / totalGoals);

            int actualAverage =
                    totalGoals == 0
                            ? 0
                            : (int) Math.round(
                                    actualSum
                                            / totalGoals);

            chart.add(
                    new BurnChartPoint(
                            currentDate.format(formatter),
                            expectedAverage,
                            actualAverage
                    )
            );
        }

        return chart;
    }
    
    public List<BurnChartPoint> generateSingleGoalChart(Goal goal) {

        List<BurnChartPoint> chart = new ArrayList<>();

        if (goal == null
                || goal.getStartDate() == null
                || goal.getTargetDate() == null) {

            return chart;
        }

        LocalDate startDate = goal.getStartDate();
        LocalDate endDate = goal.getTargetDate();

        long totalDays = ChronoUnit.DAYS.between(startDate, endDate);

        if (totalDays <= 0) {
            totalDays = 1;
        }

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd MMM");

        Set<LocalDate> chartDates = new TreeSet<>();

        chartDates.add(startDate);

        List<GoalUpdate> updates =
                goalUpdateRepository
                        .findByGoalOrderBySubmittedAtAsc(goal);

        for (GoalUpdate update : updates) {

            chartDates.add(
                    update.getSubmittedAt()
                            .toLocalDate()
            );
        }

        chartDates.add(endDate);

        for (LocalDate currentDate : chartDates) {

            /*
             * PLANNED
             */
            long elapsedDays =
                    ChronoUnit.DAYS.between(
                            startDate,
                            currentDate);

            elapsedDays = Math.min(
                    elapsedDays,
                    totalDays);

            int planned =
                    (int) Math.round(
                            (elapsedDays * 100.0)
                                    / totalDays);

            planned = Math.min(
                    100,
                    Math.max(0, planned));

            /*
             * ACTUAL
             */
            int actual = 0;

            for (GoalUpdate update : updates) {

                if (!update.getSubmittedAt()
                        .toLocalDate()
                        .isAfter(currentDate)) {

                    actual +=
                            update.getProgressPercentage();
                }
            }

            actual = Math.min(actual, 100);

            chart.add(
                    new BurnChartPoint(
                            currentDate.format(formatter),
                            planned,
                            actual
                    )
            );
        }

        return chart;
    }
    public List<BurnChartPoint> generateSingleGoalBurnChart(Goal goal) {

        List<BurnChartPoint> chart = new ArrayList<>();

        if (goal == null
                || goal.getStartDate() == null
                || goal.getTargetDate() == null) {

            return chart;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM");

        List<GoalUpdate> updates =
                goalUpdateRepository
                        .findByGoalOrderBySubmittedAtAsc(goal);

        long totalDays = ChronoUnit.DAYS.between(goal.getStartDate(), goal.getTargetDate());
        if (totalDays <= 0) totalDays = 1;

        /*
         * 1. START POINT
         */
        chart.add(
                new BurnChartPoint(
                        goal.getStartDate().format(formatter),
                        0,
                        0
                )
        );

        /*
         * 2. ACTUAL UPDATES WITH INTERPOLATED PLANNED PROGRESS
         */
        int cumulative = 0;

        for (GoalUpdate update : updates) {

            cumulative += update.getProgressPercentage();
            cumulative = Math.min(cumulative, 100);

            long elapsed = ChronoUnit.DAYS.between(goal.getStartDate(), update.getSubmittedAt().toLocalDate());
            elapsed = Math.max(0, Math.min(elapsed, totalDays));
            int plannedAtUpdate = (int) Math.round(((double) elapsed / totalDays) * 100);

            chart.add(
                    new BurnChartPoint(
                            update.getSubmittedAt().format(formatter),
                            plannedAtUpdate,
                            cumulative
                    )
            );
        }

        /*
         * 3. TARGET END POINT
         */
        chart.add(
                new BurnChartPoint(
                        goal.getTargetDate().format(formatter),
                        100,
                        cumulative
                )
        );

        return chart;
    }
}