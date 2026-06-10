package com.example.admindashboard.controller;

import com.example.admindashboard.model.Meeting;
import com.example.admindashboard.model.User;
import com.example.admindashboard.repository.MeetingRepository;
import com.example.admindashboard.repository.UserRepository;
import com.example.admindashboard.service.EmailService; // Added Email Service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/meetings")
public class MeetingController {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private UserRepository userRepository;

    // INJECT THE EMAIL SERVICE
    @Autowired
    private EmailService emailService;

    @PostMapping("/book")
    public ResponseEntity<?> bookMeeting(@RequestBody Meeting meeting, Principal principal) {
        try {
            // 1. Identify who is logged in and booking the room
            String username = principal.getName();
            User organizer = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 2. Attach the organizer to the meeting
            meeting.setOrganizer(organizer);

            // 3. Validate Date (Max 1 Year in advance & Not in the past)
            java.time.LocalDate today = java.time.LocalDate.now();
            if (meeting.getMeetingDate().isBefore(today)) {
                return ResponseEntity.badRequest().body("Cannot book meetings in the past.");
            }
            if (meeting.getMeetingDate().isAfter(today.plusYears(1))) {
                return ResponseEntity.badRequest().body("Cannot book meetings more than 1 year in advance.");
            }

            // 4. Overlapping meetings check
            List<Meeting> existingMeetings = meetingRepository.findByOrganizerAndMeetingDate(organizer, meeting.getMeetingDate());
            for (Meeting existing : existingMeetings) {
                // Check if times overlap: new_start < existing_end AND new_end > existing_start
                if (meeting.getStartTime().isBefore(existing.getEndTime()) && meeting.getEndTime().isAfter(existing.getStartTime())) {
                    return ResponseEntity.badRequest().body("You already have an overlapping meeting scheduled at this time.");
                }
            }

            // 5. Validate specific employee IDs before saving meeting
            if ("SPECIFIC_EMP".equalsIgnoreCase(meeting.getParticipantType()) || "SPECIFIC_ADM".equalsIgnoreCase(meeting.getParticipantType())) {
                if (meeting.getSpecificEmployeeIds() == null || meeting.getSpecificEmployeeIds().trim().isEmpty()) {
                    return ResponseEntity.badRequest().body("Employee/Admin IDs are required when booking for specific participants.");
                }
            }

            if (meeting.getSpecificEmployeeIds() != null && !meeting.getSpecificEmployeeIds().trim().isEmpty()) {

                String[] invitedIds = meeting.getSpecificEmployeeIds().split(",");
                List<String> invalidEmployeeIds = new ArrayList<>();

                for (String empId : invitedIds) {
                    String cleanedEmpId = empId.trim();

                    if (cleanedEmpId.isEmpty()) {
                        continue;
                    }

                    // Self-booking check
                    if (cleanedEmpId.equalsIgnoreCase(username)) {
                        return ResponseEntity.badRequest().body("You cannot book a meeting with yourself.");
                    }

                    boolean employeeExists = userRepository.findByUsername(cleanedEmpId).isPresent();

                    if (!employeeExists) {
                    	invalidEmployeeIds.add(cleanedEmpId);
                    }
                }

                if (!invalidEmployeeIds.isEmpty()) {
                    return ResponseEntity.badRequest().body(
                            "Invalid Employee ID(s): " + String.join(", ", invalidEmployeeIds)
                    );
                }
            }

            // 4. Save to the PostgreSQL database only after validation passes
            Meeting savedMeeting = meetingRepository.save(meeting);

            // --- ASYNC EMAIL TRIGGER START ---
            try {
                // Package the meeting details for the HTML template
                Map<String, Object> emailData = new HashMap<>();
                emailData.put("meetingTitle", savedMeeting.getMeetingTitle());
                emailData.put("meetingDate", savedMeeting.getMeetingDate());
                emailData.put("startTime", savedMeeting.getStartTime());
                emailData.put("endTime", savedMeeting.getEndTime());
                emailData.put("meetingMode", savedMeeting.getMeetingMode());
                emailData.put("platform", savedMeeting.getPlatform() != null ? savedMeeting.getPlatform() : "TBD");
                emailData.put("organizerName", organizer.getFullName());
                emailData.put("meetingLink", savedMeeting.getMeetingLink());

                // Check if specific employees were invited
                if (savedMeeting.getSpecificEmployeeIds() != null && !savedMeeting.getSpecificEmployeeIds().trim().isEmpty()) {

                    // Split the comma-separated string into an array (e.g., ["EMP001", "EMP002"])
                    String[] invitedIds = savedMeeting.getSpecificEmployeeIds().split(",");

                    // Loop through each ID, find them in the DB, and send the invite
                    for (String empId : invitedIds) {
                        userRepository.findByUsername(empId.trim()).ifPresent(invitee -> {
                            // Only send if they have a valid email setup
                            if (invitee.getEmail() != null && !invitee.getEmail().isEmpty()) {
                                emailService.sendMeetingInvite(
                                        invitee.getEmail(),
                                        invitee.getFullName(),
                                        savedMeeting.getMeetingTitle(),
                                        emailData
                                );
                            }
                        });
                    }
                }

                // Note: If you want to automatically email everyone in a BU when ParticipantType is "TEAM",
                // you can easily add an 'else if' block here later to fetch all users by BU and loop through them!

            } catch (Exception e) {
                System.err.println("⚠️ Warning: Could not send meeting invites: " + e.getMessage());
            }
            // --- ASYNC EMAIL TRIGGER END ---

            return ResponseEntity.ok("Meeting booked successfully!");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error booking meeting: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approveMeeting(@PathVariable Long id, Principal principal) {
        try {
            Meeting meeting = meetingRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Meeting not found"));
            
            // Assume any authenticated user can approve it for now (if they see it on their dashboard, they are invited)
            meeting.setStatus("CONFIRMED");
            meetingRepository.save(meeting);
            
            return ResponseEntity.ok("Meeting confirmed!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error approving meeting: " + e.getMessage());
        }
    }
}