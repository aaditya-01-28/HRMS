package com.example.admindashboard.controller;

import com.example.admindashboard.model.Meeting;
import com.example.admindashboard.model.User;
import com.example.admindashboard.model.EmployeeProfile;
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

    @Autowired
    private EmailService emailService;

    @Autowired
    private com.example.admindashboard.repository.TeamRepository teamRepository;

    @Autowired
    private com.example.admindashboard.repository.TeamMemberRepository teamMemberRepository;

    @PostMapping("/book")
    public ResponseEntity<?> bookMeeting(@RequestBody Meeting meeting, Principal principal) {
        try {
            // 1. Identify who is logged in and booking the room
            String username = principal.getName();
            User organizer = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 2. Attach the organizer to the meeting
            meeting.setOrganizer(organizer);

            // 3. Validate Date & Time fields are not null
            if (meeting.getMeetingDate() == null || meeting.getStartTime() == null || meeting.getEndTime() == null) {
                return ResponseEntity.badRequest().body("Meeting Date, Start Time, and End Time are required.");
            }

            java.time.LocalDate today = java.time.LocalDate.now();
            if (meeting.getMeetingDate().isBefore(today)) {
                return ResponseEntity.badRequest().body("Cannot book meetings in the past.");
            }
            if (meeting.getMeetingDate().getYear() > today.getYear()) {
                return ResponseEntity.badRequest().body("Cannot book a meeting for a future year. Invalid year.");
            }

            // 4. Overlapping meetings check
            List<Meeting> existingMeetings = meetingRepository.findByOrganizerAndMeetingDate(organizer, meeting.getMeetingDate());
            for (Meeting existing : existingMeetings) {
                // Check if times overlap: new_start < existing_end AND new_end > existing_start
                if (meeting.getStartTime().isBefore(existing.getEndTime()) && meeting.getEndTime().isAfter(existing.getStartTime())) {
                    return ResponseEntity.badRequest().body("You already have an overlapping meeting scheduled at this time.");
                }
            }

            // 5. Resolve Team Members if participantType is TEAM
            if ("TEAM".equalsIgnoreCase(meeting.getParticipantType())) {
                java.util.Set<String> teamMemberUsernames = new java.util.LinkedHashSet<>();
                
                // a. If organizer is a manager of Teams
                try {
                    List<com.example.admindashboard.model.Team> managedTeams = teamRepository.findByManager(organizer);
                    if (managedTeams != null) {
                        for (com.example.admindashboard.model.Team t : managedTeams) {
                            if (t.getMembers() != null) {
                                for (com.example.admindashboard.model.TeamMember tm : t.getMembers()) {
                                    if (tm.getUser() != null && !tm.getUser().getUsername().equalsIgnoreCase(username)) {
                                        teamMemberUsernames.add(tm.getUser().getUsername());
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception ignored) {}

                // b. If organizer is a member of Teams
                try {
                    List<com.example.admindashboard.model.TeamMember> memberships = teamMemberRepository.findByUser(organizer);
                    if (memberships != null) {
                        for (com.example.admindashboard.model.TeamMember tm : memberships) {
                            if (tm.getTeam() != null) {
                                if (tm.getTeam().getManager() != null && !tm.getTeam().getManager().getUsername().equalsIgnoreCase(username)) {
                                    teamMemberUsernames.add(tm.getTeam().getManager().getUsername());
                                }
                                if (tm.getTeam().getMembers() != null) {
                                    for (com.example.admindashboard.model.TeamMember peer : tm.getTeam().getMembers()) {
                                        if (peer.getUser() != null && !peer.getUser().getUsername().equalsIgnoreCase(username)) {
                                            teamMemberUsernames.add(peer.getUser().getUsername());
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception ignored) {}

                // c. Direct reports & reporting hierarchy
                List<User> allUsers = userRepository.findAll();
                String organizerFullName = organizer.getFullName();
                EmployeeProfile organizerProfile = organizer.getEmployeeProfile();
                String organizerReportingManager = organizerProfile != null ? organizerProfile.getReportingManager() : null;
                String bu = organizerProfile != null ? organizerProfile.getBusinessUnit() : null;

                for (User u : allUsers) {
                    if (u.getUsername().equalsIgnoreCase(username)) continue;
                    
                    // Direct reports
                    if (u.getManager() != null && u.getManager().getId().equals(organizer.getId())) {
                        teamMemberUsernames.add(u.getUsername());
                    } else if (u.getEmployeeProfile() != null && organizerFullName != null && organizerFullName.equalsIgnoreCase(u.getEmployeeProfile().getReportingManager())) {
                        teamMemberUsernames.add(u.getUsername());
                    }
                    // Peers with same reporting manager
                    else if (organizerReportingManager != null && u.getEmployeeProfile() != null && organizerReportingManager.equalsIgnoreCase(u.getEmployeeProfile().getReportingManager())) {
                        teamMemberUsernames.add(u.getUsername());
                    }
                    // Same Business Unit fallback
                    else if (bu != null && u.getEmployeeProfile() != null && bu.equalsIgnoreCase(u.getEmployeeProfile().getBusinessUnit())) {
                        teamMemberUsernames.add(u.getUsername());
                    }
                }

                // If organizer has a manager, include manager for notification/approval
                if (organizer.getManager() != null && !organizer.getManager().getUsername().equalsIgnoreCase(username)) {
                    teamMemberUsernames.add(organizer.getManager().getUsername());
                }

                if (!teamMemberUsernames.isEmpty()) {
                    meeting.setSpecificEmployeeIds(String.join(",", teamMemberUsernames));
                }
            }

            // 6. Validate specific employee/admin IDs
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

            // Status is PENDING approval by default
            meeting.setStatus("PENDING");

            // 7. Save to the database only after validation passes
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

                // Send email invite to all invited IDs
                if (savedMeeting.getSpecificEmployeeIds() != null && !savedMeeting.getSpecificEmployeeIds().trim().isEmpty()) {
                    String[] invitedIds = savedMeeting.getSpecificEmployeeIds().split(",");
                    for (String empId : invitedIds) {
                        userRepository.findByUsername(empId.trim()).ifPresent(invitee -> {
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
            
            String username = principal.getName();
            String approved = meeting.getApprovedEmployeeIds();
            if (approved == null || approved.trim().isEmpty()) {
                meeting.setApprovedEmployeeIds(username);
            } else {
                List<String> list = new ArrayList<>(List.of(approved.split(",")));
                if (!list.contains(username)) {
                    list.add(username);
                    meeting.setApprovedEmployeeIds(String.join(",", list));
                }
            }

            // Check if all specific employees have approved
            if ("SPECIFIC_EMP".equalsIgnoreCase(meeting.getParticipantType()) || "SPECIFIC_ADM".equalsIgnoreCase(meeting.getParticipantType())) {
                String invited = meeting.getSpecificEmployeeIds();
                if (invited != null && !invited.trim().isEmpty()) {
                    String[] invitedArr = invited.split(",");
                    boolean allApproved = true;
                    String approvedStr = meeting.getApprovedEmployeeIds();
                    List<String> approvedList = approvedStr == null ? new ArrayList<>() : List.of(approvedStr.split(","));
                    for (String inv : invitedArr) {
                        if (!approvedList.contains(inv.trim())) {
                            allApproved = false;
                            break;
                        }
                    }
                    if (allApproved) {
                        meeting.setStatus("CONFIRMED");
                    }
                }
            } else {
                // For other types, any approval tracks it in approvedEmployeeIds.
                // It remains PENDING, but is confirmed for that individual user side in queries.
            }
            
            meetingRepository.save(meeting);
            
            return ResponseEntity.ok("Meeting confirmed!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error approving meeting: " + e.getMessage());
        }
    }
}