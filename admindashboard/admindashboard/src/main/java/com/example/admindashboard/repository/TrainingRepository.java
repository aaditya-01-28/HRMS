package com.example.admindashboard.repository;

import com.example.admindashboard.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TrainingRepository
        extends JpaRepository<Training, Long> {
	List<Training> findByTrainingType(String trainingType);

	List<Training> findByStatus(String status);

	List<Training> findByTitleContainingIgnoreCase(String title);

	List<Training> findByTrainingTypeAndStatus(
	        String trainingType,
	        String status);

}