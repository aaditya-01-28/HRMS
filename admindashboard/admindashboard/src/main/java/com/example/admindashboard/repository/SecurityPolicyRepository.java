package com.example.admindashboard.repository;

import com.example.admindashboard.model.SecurityPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecurityPolicyRepository extends JpaRepository<SecurityPolicy, Long> {
}
