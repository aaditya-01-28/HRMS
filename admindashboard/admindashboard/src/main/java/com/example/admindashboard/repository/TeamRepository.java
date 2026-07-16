package com.example.admindashboard.repository;

import com.example.admindashboard.model.Team;
import com.example.admindashboard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT t FROM Team t LEFT JOIN FETCH t.members m LEFT JOIN FETCH m.user WHERE t.manager = :manager")
    List<Team> findByManager(@org.springframework.data.repository.query.Param("manager") User manager);
}
