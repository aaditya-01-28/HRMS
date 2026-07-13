package com.example.admindashboard.repository;

import com.example.admindashboard.model.CommunicationBroadcast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunicationBroadcastRepository extends JpaRepository<CommunicationBroadcast, Long> {
}
