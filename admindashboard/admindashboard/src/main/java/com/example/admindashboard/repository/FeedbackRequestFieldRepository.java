package com.example.admindashboard.repository;

import com.example.admindashboard.model.FeedbackRequestField;
import com.example.admindashboard.model.PerformanceFeedbackRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRequestFieldRepository extends JpaRepository<FeedbackRequestField, Long> {
    List<FeedbackRequestField> findByFeedbackRequest(PerformanceFeedbackRequest request);
}
