package com.example.admindashboard.repository;

import com.example.admindashboard.model.LearningProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LearningProgramRepository extends JpaRepository<LearningProgram, Long> {
}
