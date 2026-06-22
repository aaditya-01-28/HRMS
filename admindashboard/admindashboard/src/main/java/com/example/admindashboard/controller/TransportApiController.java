package com.example.admindashboard.controller;

import com.example.admindashboard.model.*;
import com.example.admindashboard.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/transport")
public class TransportApiController {

    @Autowired
    private TransportVendorRepository vendorRepository;

    @Autowired
    private TransportDriverRepository driverRepository;

    @Autowired
    private TransportVehicleRepository vehicleRepository;

    @Autowired
    private RideBookingRepository rideBookingRepository;

    @GetMapping("/vendors")
    public List<TransportVendor> getAllVendors() {
        return vendorRepository.findAll();
    }

    @PostMapping("/vendors")
    public TransportVendor createVendor(@RequestBody TransportVendor vendor) {
        vendor.setJoiningDate(java.time.LocalDate.now());
        return vendorRepository.save(vendor);
    }

    @PostMapping("/vendors/{vendorId}/vehicles")
    public TransportVehicle createVehicle(@PathVariable Long vendorId, @RequestBody TransportVehicle vehicle) {
        TransportVendor vendor = vendorRepository.findById(vendorId).orElseThrow(() -> new RuntimeException("Vendor not found"));
        vehicle.setVendor(vendor);
        return vehicleRepository.save(vehicle);
    }

    @PostMapping("/vendors/{vendorId}/drivers")
    public TransportDriver createDriver(@PathVariable Long vendorId, @RequestBody TransportDriver driver) {
        TransportVendor vendor = vendorRepository.findById(vendorId).orElseThrow(() -> new RuntimeException("Vendor not found"));
        driver.setVendor(vendor);
        return driverRepository.save(driver);
    }

    @GetMapping("/vehicles")
    public List<TransportVehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    @PostMapping("/bookings/assign")
    public ResponseEntity<?> assignVehicleToBookings(@RequestBody Map<String, Object> payload) {
        List<Integer> bookingIdsList = (List<Integer>) payload.get("bookingIds");
        Long vehicleId = Long.valueOf(payload.get("vehicleId").toString());

        Optional<TransportVehicle> vehicleOpt = vehicleRepository.findById(vehicleId);
        if (vehicleOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Vehicle not found");
        }
        TransportVehicle vehicle = vehicleOpt.get();

        for (Integer bId : bookingIdsList) {
            Optional<RideBooking> bookingOpt = rideBookingRepository.findById(bId.longValue());
            if (bookingOpt.isPresent()) {
                RideBooking booking = bookingOpt.get();
                booking.setVehicle(vehicle);
                booking.setStatus("Assigned");
                rideBookingRepository.save(booking);
            }
        }
        
        // Update vehicle status
        vehicle.setStatus("Assigned");
        vehicleRepository.save(vehicle);

        return ResponseEntity.ok(Map.of("message", "Assigned successfully"));
    }
}
