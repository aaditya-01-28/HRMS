package com.example.admindashboard.service;

import com.example.admindashboard.dto.LearningDashboardStats;
import com.example.admindashboard.repository.CourseEnrollmentRepository;
import com.example.admindashboard.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.admindashboard.dto.LearningActivityDTO;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import com.example.admindashboard.model.CourseEnrollment;

@Service
public class LearningDashboardService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseEnrollmentRepository enrollmentRepository;

    public LearningDashboardStats getDashboardStats() {

        LearningDashboardStats stats =
                new LearningDashboardStats();

        stats.setTotalCourses(
                courseRepository.count());

        stats.setActiveLearners(
                enrollmentRepository.count());

        stats.setPendingApprovals(
                enrollmentRepository.countByStatus("ASSIGNED"));

        stats.setCompletedCourses(
                enrollmentRepository.countByStatus("COMPLETED"));

        stats.setCertificatesIssued(
                enrollmentRepository.countByStatus("COMPLETED"));

        return stats;
    }
    public LearningActivityDTO getLearningActivity() {

        LearningActivityDTO dto =
                new LearningActivityDTO();

        dto.setNewEnrollments(
                enrollmentRepository.countByAssignedDate(
                        LocalDate.now()));

        dto.setInProgress(
                enrollmentRepository.countByStatus(
                        "IN_PROGRESS"));

        dto.setCompleted(
                enrollmentRepository.countByStatus(
                        "COMPLETED"));

        dto.setOverdue(
                enrollmentRepository.countByStatus(
                        "OVERDUE"));

        return dto;
    }
    public List<Integer> getEnrollmentTrend() {

        return List.of(
                2,
                4,
                4,
                6,
                5,
                7,
                9,
                8,
                10
        );
    }
    public List<CourseEnrollment> getPendingApprovals() {
        return enrollmentRepository
                .findTop5ByStatusOrderByAssignedDateDesc("ASSIGNED");
    }
    public Map<String, Long> getCategoryDistribution() {

        Map<String, Long> result =
                new LinkedHashMap<>();

        for(Object[] row :
                courseRepository.getCategoryDistribution()) {

            result.put(
                    String.valueOf(row[0]),
                    (Long) row[1]);
        }

        return result;
    }
    public List<CourseEnrollment> getApprovalRequests() {

        return enrollmentRepository.findAll();
    }
}