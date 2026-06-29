package com.example.admindashboard.repository;

import com.example.admindashboard.model.ResignationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResignationRequestRepository extends JpaRepository<ResignationRequest, Long> {
    List<ResignationRequest> findByEmployee_Username(String username);
    List<ResignationRequest> findByStatus(String status);
    Optional<ResignationRequest> findByEmployee_UsernameAndStatusNot(String username, String status);
}
