package com.example.admindashboard.repository;

import com.example.admindashboard.model.RecentNotificationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecentNotificationRecordRepository extends JpaRepository<RecentNotificationRecord, Long> {
}
