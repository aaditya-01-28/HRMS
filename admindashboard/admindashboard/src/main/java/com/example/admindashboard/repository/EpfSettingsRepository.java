package com.example.admindashboard.repository;

import com.example.admindashboard.model.EpfSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EpfSettingsRepository extends JpaRepository<EpfSettings, Long> {
}
