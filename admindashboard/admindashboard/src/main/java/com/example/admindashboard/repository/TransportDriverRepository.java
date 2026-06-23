package com.example.admindashboard.repository;

import com.example.admindashboard.model.TransportDriver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransportDriverRepository extends JpaRepository<TransportDriver, Long> {
    List<TransportDriver> findByVendorId(Long vendorId);
}
