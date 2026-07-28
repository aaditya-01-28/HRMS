package com.example.admindashboard.repository;

import com.example.admindashboard.model.ClassificationConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassificationConfigRepository extends JpaRepository<ClassificationConfig, Long> {
}
