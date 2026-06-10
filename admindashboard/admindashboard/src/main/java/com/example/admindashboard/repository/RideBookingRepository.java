package com.example.admindashboard.repository;

import com.example.admindashboard.model.RideBooking;
import com.example.admindashboard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RideBookingRepository extends JpaRepository<RideBooking, Long> {
    List<RideBooking> findByUserOrderByRideDateAsc(User user);
    Optional<RideBooking> findByUserAndRideDate(User user, LocalDate rideDate);
}
