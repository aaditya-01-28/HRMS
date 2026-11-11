package com.example.admindashboard.repository;

import com.example.admindashboard.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    Optional<Candidate> findByName(String name);
    List<Candidate> findByOnboardStatus(String onboardStatus);
    List<Candidate> findByOnboardStatusIsNotNull();
}
