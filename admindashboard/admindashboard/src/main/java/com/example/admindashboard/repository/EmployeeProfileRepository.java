package com.example.admindashboard.repository;

import com.example.admindashboard.model.EmployeeProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeProfileRepository extends JpaRepository<EmployeeProfile, Long> {

    // The underscore tells Spring to look inside the 'user' object for the 'username'
    Optional<EmployeeProfile> findByUser_Username(String username);

    Optional<EmployeeProfile> findByUser_Id(Long userId);

    Optional<EmployeeProfile> findByEmployeeCode(String employeeCode);

    List<EmployeeProfile> findByDepartment(String department);

    List<EmployeeProfile> findByDepartmentIgnoreCase(String department);

    long countByDepartmentIgnoreCase(String department);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM employee_profiles WHERE id = :id", nativeQuery = true)
    void deleteProfileByIdNative(@Param("id") Long id);
}
