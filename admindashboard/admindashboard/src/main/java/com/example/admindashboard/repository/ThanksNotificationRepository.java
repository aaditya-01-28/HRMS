package com.example.admindashboard.repository;

import com.example.admindashboard.model.ThanksNotification;
import com.example.admindashboard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThanksNotificationRepository extends JpaRepository<ThanksNotification, Long> {
    List<ThanksNotification> findByUserOrderByCreatedAtDesc(User user);
    int countByUserAndReadFalse(User user);
}
