package com.example.admindashboard.repository;

import com.example.admindashboard.model.EstablishmentSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstablishmentSettingsRepository extends JpaRepository<EstablishmentSettings, Long> {
}
