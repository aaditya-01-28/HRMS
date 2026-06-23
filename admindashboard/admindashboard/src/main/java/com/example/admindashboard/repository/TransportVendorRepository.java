package com.example.admindashboard.repository;

import com.example.admindashboard.model.TransportVendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransportVendorRepository extends JpaRepository<TransportVendor, Long> {
}
