package com.example.admindashboard.repository;

import com.example.admindashboard.model.AttendanceRegularization;
import com.example.admindashboard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRegularizationRepository
        extends JpaRepository<AttendanceRegularization, Long> {

    List<AttendanceRegularization> findByStatus(String status);
    List<AttendanceRegularization> findByUser(User user);

    List<AttendanceRegularization>
    findByUserAndDate(
            User user,
            LocalDate date
    );
}