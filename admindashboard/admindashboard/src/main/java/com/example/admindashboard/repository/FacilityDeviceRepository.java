package com.example.admindashboard.repository;

import com.example.admindashboard.model.FacilityDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilityDeviceRepository extends JpaRepository<FacilityDevice, Long> {
}
