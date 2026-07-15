package com.example.admindashboard.repository;

import com.example.admindashboard.model.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {

    List<ServiceRequest> findByEmployeeIdOrderBySubmissionDateDesc(String employeeId);

    List<ServiceRequest> findTop3ByEmployeeIdOrderBySubmissionDateDesc(String employeeId);

    List<ServiceRequest> findTop3ByEmployeeIdOrderByIdDesc(String employeeId);

    List<ServiceRequest> findByDepartmentOrderByIdDesc(String department);

    List<ServiceRequest> findByDepartmentAndStatusOrderByIdDesc(
            String department,
            String status
    );
    List<ServiceRequest> findByTypeOrderByIdDesc(String type);

    List<ServiceRequest> findByTypeAndStatusOrderByIdDesc(
            String type,
            String status
    );

    java.util.Optional<ServiceRequest> findByTicketId(String ticketId);
}