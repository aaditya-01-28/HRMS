package com.example.admindashboard.repository;

import com.example.admindashboard.model.CourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {

    long countByStatus(String status);

    long countByAssignedDate(java.time.LocalDate assignedDate);

    List<CourseEnrollment> findTop5ByStatusOrderByAssignedDateDesc(String status);

    @Modifying
    @Transactional
    @Query("DELETE FROM CourseEnrollment c WHERE c.employee.id = :profileId")
    void deleteByEmployeeId(@Param("profileId") Long profileId);
}
