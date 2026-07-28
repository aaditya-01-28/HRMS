package com.example.admindashboard.repository;

import com.example.admindashboard.model.AddressSetup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressSetupRepository extends JpaRepository<AddressSetup, Long> {
}
