package com.example.admindashboard.repository;

import com.example.admindashboard.model.EmployeeGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeGroupRepository extends JpaRepository<EmployeeGroup, Long> {
    Optional<EmployeeGroup> findByGroupName(String groupName);
}
