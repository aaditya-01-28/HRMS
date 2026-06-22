package com.example.admindashboard.repository;

import com.example.admindashboard.model.CourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourseEnrollmentRepository
extends JpaRepository<CourseEnrollment, Long> {

long countByStatus(String status);

long countByAssignedDate(java.time.LocalDate assignedDate);

List<CourseEnrollment> findTop5ByStatusOrderByAssignedDateDesc(
    String status);

}