package com.example.admindashboard.repository;

import com.example.admindashboard.model.DepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentEntityRepository extends JpaRepository<DepartmentEntity, Long> {
    Optional<DepartmentEntity> findByDepartmentName(String departmentName);
    Optional<DepartmentEntity> findByDepartmentNameIgnoreCase(String departmentName);
}
