package com.example.admindashboard.repository;

import com.example.admindashboard.model.TransportVehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransportVehicleRepository extends JpaRepository<TransportVehicle, Long> {
    List<TransportVehicle> findByVendorId(Long vendorId);
}
