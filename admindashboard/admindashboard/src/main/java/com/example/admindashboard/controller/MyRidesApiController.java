package com.example.admindashboard.controller;

import com.example.admindashboard.model.RideBooking;
import com.example.admindashboard.model.User;
import com.example.admindashboard.repository.RideBookingRepository;
import com.example.admindashboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@RestController
@RequestMapping("/api/my-rides")
public class MyRidesApiController {

    @Autowired
    private RideBookingRepository rideBookingRepository;

    @Autowired
    private UserRepository userRepository;

    private User getAuthenticatedUser(Principal principal) {
        if (principal == null) return null;
        return userRepository.findByUsername(principal.getName()).orElse(null);
    }

    @GetMapping("/bookings")
    public ResponseEntity<?> getMyBookings(Principal principal) {
        User user = getAuthenticatedUser(principal);
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        List<RideBooking> bookings = rideBookingRepository.findByUserOrderByRideDateAsc(user);
        
        // Format response for frontend consumption
        List<Map<String, Object>> response = new ArrayList<>();
        DateTimeFormatter fullFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale.ENGLISH);
        DateTimeFormatter shortFormatter = DateTimeFormatter.ofPattern("EEE, dd MMMM", Locale.ENGLISH);
        
        for (RideBooking booking : bookings) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", booking.getId());
            map.put("rideDateRaw", booking.getRideDate().toString());
            map.put("fullDate", booking.getRideDate().format(fullFormatter));
            map.put("shortDate", booking.getRideDate().format(shortFormatter));
            map.put("login", booking.getLoginTime());
            map.put("logout", booking.getLogoutTime());
            map.put("status", booking.getStatus());
            map.put("cancelReason", booking.getCancelReason());
            map.put("cancelRemarks", booking.getCancelRemarks());
            response.add(map);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/book")
    public ResponseEntity<?> bookRide(@RequestBody BookRideRequest request, Principal principal) {
        User user = getAuthenticatedUser(principal);
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        try {
            // date can come in formats like "EEEE, dd MMMM yyyy"
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale.ENGLISH);
            LocalDate date = LocalDate.parse(request.getDate(), formatter);

            if (date.isBefore(LocalDate.now())) {
                return ResponseEntity.badRequest().body("Cannot book rides for past dates.");
            }

            // Check if already booked
            Optional<RideBooking> existing = rideBookingRepository.findByUserAndRideDate(user, date);
            RideBooking booking = existing.orElse(new RideBooking());

            booking.setUser(user);
            booking.setRideDate(date);
            booking.setLoginTime(request.getLoginTime());
            booking.setLogoutTime(request.getLogoutTime());
            booking.setStatus("SCHEDULED");
            booking.setCancelReason(null);
            booking.setCancelRemarks(null);

            rideBookingRepository.save(booking);

            return ResponseEntity.ok(Map.of("success", true, "message", "Ride booked successfully!"));

        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Invalid date format. Expected: EEEE, dd MMMM yyyy");
        }
    }

    @PostMapping("/cancel")
    public ResponseEntity<?> cancelRide(@RequestBody CancelRideRequest request, Principal principal) {
        User user = getAuthenticatedUser(principal);
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        try {
            // Find the exact booking by short date (e.g., "Mon, 08 June" or "Mon, 08 June 2026")
            // Since frontend might send different formats, let's allow finding by ID if possible, 
            // but for now the frontend only has the full string.
            // Wait, we can parse the frontend date string.
            
            // To be safe, let's parse different formats:
            DateTimeFormatter fullFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale.ENGLISH);
            LocalDate date = null;
            
            try {
                // Try full format e.g. "Monday, 08 June 2026"
                date = LocalDate.parse(request.getDateStr(), fullFormatter);
            } catch (Exception e1) {
                try {
                    // Try short format from cancel panel e.g. "Mon, 08 June 2026"
                    DateTimeFormatter cancelFormatter = DateTimeFormatter.ofPattern("EEE, dd MMMM yyyy", Locale.ENGLISH);
                    date = LocalDate.parse(request.getDateStr(), cancelFormatter);
                } catch (Exception e2) {
                    return ResponseEntity.badRequest().body("Cannot parse date: " + request.getDateStr());
                }
            }

            Optional<RideBooking> bookingOpt = rideBookingRepository.findByUserAndRideDate(user, date);
            if (bookingOpt.isPresent()) {
                RideBooking booking = bookingOpt.get();
                booking.setStatus("CANCELLED");
                booking.setCancelReason(request.getReason());
                booking.setCancelRemarks(request.getRemarks());
                rideBookingRepository.save(booking);
                return ResponseEntity.ok(Map.of("success", true, "message", "Ride cancelled successfully!"));
            } else {
                return ResponseEntity.badRequest().body("Booking not found for date: " + date.toString());
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    // --- DTOs ---

    public static class BookRideRequest {
        private String date;
        private String loginTime;
        private String logoutTime;

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public String getLoginTime() { return loginTime; }
        public void setLoginTime(String loginTime) { this.loginTime = loginTime; }
        public String getLogoutTime() { return logoutTime; }
        public void setLogoutTime(String logoutTime) { this.logoutTime = logoutTime; }
    }

    public static class CancelRideRequest {
        private String dateStr;
        private String reason;
        private String remarks;

        public String getDateStr() { return dateStr; }
        public void setDateStr(String dateStr) { this.dateStr = dateStr; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        public String getRemarks() { return remarks; }
        public void setRemarks(String remarks) { this.remarks = remarks; }
    }
}
