package com.example.admindashboard.repository;

import com.example.admindashboard.model.HrmsNotification;
import com.example.admindashboard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HrmsNotificationRepository extends JpaRepository<HrmsNotification, Long> {
    List<HrmsNotification> findByUserOrderByCreatedAtDesc(User user);
    int countByUserAndReadFalse(User user);
}
