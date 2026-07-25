package com.example.admindashboard.repository;

import com.example.admindashboard.model.EsiSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EsiSettingsRepository extends JpaRepository<EsiSettings, Long> {
}
