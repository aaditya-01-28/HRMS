package com.example.admindashboard.repository;

import com.example.admindashboard.model.EmployeeRoleAssignment;
import com.example.admindashboard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRoleAssignmentRepository extends JpaRepository<EmployeeRoleAssignment, Long> {
    List<EmployeeRoleAssignment> findByUser(User user);
    List<EmployeeRoleAssignment> findByUserId(Long userId);
    void deleteByUserIdAndRoleId(Long userId, Long roleId);
}
