package com.example.admindashboard.service;

import com.example.admindashboard.model.Attendance;
import com.example.admindashboard.repository.AttendanceRegularizationRepository;
import java.time.DayOfWeek;
import java.util.ArrayList;
import com.example.admindashboard.dto.AttendanceDayDTO;
import com.example.admindashboard.model.User;
import com.example.admindashboard.repository.AttendanceRepository;
import com.example.admindashboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.admindashboard.dto.AttendanceRegularizationRequestDTO;
import com.example.admindashboard.model.AttendanceRegularization;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AttendanceRegularizationRepository attendanceRegularizationRepository;

    // Logic: Employee Check-In
    public String checkIn(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) return "User not found!";
        User user = userOpt.get();

        LocalDate today = LocalDate.now();

        // Rule: Prevent Duplicate Entry
        Optional<Attendance> existing = attendanceRepository.findByUserAndDate(user, today);
        if (existing.isPresent()) {
            return "You have already checked in today!";
        }

        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setDate(today);
        attendance.setCheckInTime(LocalTime.now());
        attendance.setStatus("Present");

        attendanceRepository.save(attendance);
        return "Check-In Successful at " + LocalTime.now();
    }

    // Logic: Employee Check-Out
    public String checkOut(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) return "User not found!";
        User user = userOpt.get();

        LocalDate today = LocalDate.now();

        // Rule: No Checkout without Checkin
        Optional<Attendance> existing = attendanceRepository.findByUserAndDate(user, today);
        if (existing.isEmpty()) {
            return "You have not checked in today!";
        }

        Attendance attendance = existing.get();
        if (attendance.getCheckOutTime() != null) {
            return "You have already checked out today!";
        }

        attendance.setCheckOutTime(LocalTime.now());

        // Rule: Auto Calculate Total Hours
        long minutes = Duration.between(attendance.getCheckInTime(), attendance.getCheckOutTime()).toMinutes();
        long hours = minutes / 60;
        long remainingMinutes = minutes % 60;
        attendance.setTotalHours(hours + "h " + remainingMinutes + "m");

        attendanceRepository.save(attendance);
        return "Check-Out Successful. Total Hours: " + attendance.getTotalHours();
    }

    // --- ADMIN FEATURE ---
    // Count how many people checked in today
    public long getTodayPresentCount() {
        LocalDate today = LocalDate.now();
        return attendanceRepository.countByDate(today);
    }

    // ==========================================
    // ATTENDANCE REGULATION (WEEKLY) LOGIC
    // ==========================================
    public Attendance saveWeeklyDraft(java.util.Map<String, Object> data, String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) throw new RuntimeException("User not found!");
        User user = userOpt.get();

        String weekStartDateStr = (String) data.get("weekStartDate");
        LocalDate weekStartDate = LocalDate.parse(weekStartDateStr);
        
        // duplicate-check for submission
        String weekEndDateStr = weekStartDate.plusDays(6).toString();

        List<Attendance> existingWeeklyAttendance =
                attendanceRepository.findByUserAndWeekStartDateAndWeekEndDate(user,weekStartDate.toString(),weekEndDateStr );

        Attendance attendance;

        if (!existingWeeklyAttendance.isEmpty()) {
            attendance = existingWeeklyAttendance.get(0);

            if (!"Draft".equalsIgnoreCase(attendance.getApprovalStatus())) {
                throw new RuntimeException("You have already submitted an attendance regularization request for this week.");
            }
        } else {
            attendance = new Attendance();
            attendance.setUser(user);
            attendance.setWeekStartDate(weekStartDate.toString());
            attendance.setWeekEndDate(weekEndDateStr);
            attendance.setApprovalStatus("Draft");
        }

        double totalHours = 0.0;
        int presentDays = 0;
        int absentDays = 0;
        String generalReason = "Weekly attendance submission.";

        String[] days = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};
        for (String day : days) {
            Object hoursObj = data.get(day + "Hours");
            double hours = hoursObj != null ? Double.parseDouble(hoursObj.toString()) : 0.0;
            totalHours += hours;

            String status = (String) data.get(day + "Status");
            if ("Absent".equalsIgnoreCase(status)) {
                absentDays++;
            } else if (hours > 0 || "Present".equalsIgnoreCase(status) || "Present (Late)".equalsIgnoreCase(status)) {
                presentDays++;
            }

            String reason = (String) data.get(day + "Reason");
            if (reason != null && !reason.trim().isEmpty() && generalReason.equals("Weekly attendance submission.")) {
                generalReason = reason;
            }
        }

        attendance.setTotalHours(String.valueOf(totalHours));
        attendance.setPresentDays(presentDays);
        attendance.setAbsentDays(absentDays);
        attendance.setReason(generalReason);
        attendance.setApprovalStatus("Draft");

        // --- SAVE DAILY BREAKDOWNS ---
        attendance.setMondayHours(data.get("mondayHours") != null ? Double.parseDouble(data.get("mondayHours").toString()) : 0.0);
        attendance.setMondayStatus((String) data.get("mondayStatus"));
        attendance.setMondayMode((String) data.get("mondayMode"));
        attendance.setMondayReason((String) data.get("mondayReason"));

        attendance.setTuesdayHours(data.get("tuesdayHours") != null ? Double.parseDouble(data.get("tuesdayHours").toString()) : 0.0);
        attendance.setTuesdayStatus((String) data.get("tuesdayStatus"));
        attendance.setTuesdayMode((String) data.get("tuesdayMode"));
        attendance.setTuesdayReason((String) data.get("tuesdayReason"));

        attendance.setWednesdayHours(data.get("wednesdayHours") != null ? Double.parseDouble(data.get("wednesdayHours").toString()) : 0.0);
        attendance.setWednesdayStatus((String) data.get("wednesdayStatus"));
        attendance.setWednesdayMode((String) data.get("wednesdayMode"));
        attendance.setWednesdayReason((String) data.get("wednesdayReason"));

        attendance.setThursdayHours(data.get("thursdayHours") != null ? Double.parseDouble(data.get("thursdayHours").toString()) : 0.0);
        attendance.setThursdayStatus((String) data.get("thursdayStatus"));
        attendance.setThursdayMode((String) data.get("thursdayMode"));
        attendance.setThursdayReason((String) data.get("thursdayReason"));

        attendance.setFridayHours(data.get("fridayHours") != null ? Double.parseDouble(data.get("fridayHours").toString()) : 0.0);
        attendance.setFridayStatus((String) data.get("fridayStatus"));
        attendance.setFridayMode((String) data.get("fridayMode"));
        attendance.setFridayReason((String) data.get("fridayReason"));
        
        attendance.setSaturdayHours(
        	    data.get("saturdayHours") != null
        	        ? Double.parseDouble(data.get("saturdayHours").toString())
        	        : 0.0
        	);

        	attendance.setSaturdayStatus(
        	    (String) data.get("saturdayStatus")
        	);

        	attendance.setSaturdayMode(
        	    (String) data.get("saturdayMode")
        	);

        	attendance.setSaturdayReason(
        	    (String) data.get("saturdayReason")
        	);

        return attendanceRepository.save(attendance);
    }
    

    public void submitWeeklyAttendance(Long id, String username) {
        // 1. Find the saved draft
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attendance record not found"));

        // 2. Security Check: Make sure the logged-in user owns this record
        if (!attendance.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You are not authorized to submit this record.");
        }
        
        /*int totalMinutes = 0;
        int presentDays = 0;

        for (int i = 0; i < 7; i++) {

            LocalDate currentDate =
                    monday.plusDays(i);

            List<AttendanceRegularization> records =
                    attendanceRegularizationRepository
                            .findByUserAndDate(
                                    user,
                                    currentDate
                            );

            if (!records.isEmpty()) {
                presentDays++;
            }

            for (AttendanceRegularization record : records) {

                String duration =
                        record.getDuration();

                if (duration == null) {
                    continue;
                }

                java.util.regex.Matcher matcher =
                        java.util.regex.Pattern
                                .compile("(\\d+)hr\\s*(\\d+)min")
                                .matcher(duration);

                if (matcher.find()) {

                    totalMinutes +=
                            Integer.parseInt(matcher.group(1)) * 60;

                    totalMinutes +=
                            Integer.parseInt(matcher.group(2));
                }
            }
        }

        attendance.setPresentDays(presentDays);
        attendance.setAbsentDays(5 - presentDays);

        int hrs = totalMinutes / 60;
        int mins = totalMinutes % 60;

        attendance.setTotalHours(
                hrs + "." + mins
        );*/

        // 3. Update status to Pending so the Manager sees it in My Approvals
        attendance.setApprovalStatus("Pending");
        attendance.setSubmittedOn(LocalDate.now());

        attendanceRepository.save(attendance);
    }

    // Fetch Attendance History for the logged-in user
    public List<Attendance> getMyAttendanceHistory(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            return attendanceRepository.findByUserOrderByIdDesc(userOpt.get());
        }
        return java.util.List.of(); // Return empty list if user not found
    }
    public List<AttendanceDayDTO> getCurrentWeekAttendance(
            User user,
            LocalDate monday) {

        List<AttendanceDayDTO> result = new ArrayList<>();
        String weekStatus = "Draft";

        LocalDate mondayDate = monday;

        String weekEndDate =
                mondayDate.plusDays(6).toString();

        List<Attendance> weeklyAttendance =
                attendanceRepository
                        .findByUserAndWeekStartDateAndWeekEndDate(
                                user,
                                mondayDate.toString(),
                                weekEndDate
                        );

        if (weeklyAttendance.isEmpty()) {

            weekStatus = "Draft";

        } else {

            Attendance latestAttendance =
                    weeklyAttendance.get(0);

            boolean hasRecordedTime =
                    attendanceRegularizationRepository
                            .findByUserAndDate(
                                    user,
                                    mondayDate
                            )
                            .size() > 0;

            for (int i = 1; i < 7 && !hasRecordedTime; i++) {

                hasRecordedTime =
                        attendanceRegularizationRepository
                                .findByUserAndDate(
                                        user,
                                        mondayDate.plusDays(i)
                                )
                                .size() > 0;
            }

            if (!hasRecordedTime) {

                weekStatus = "Draft";

            } else if (latestAttendance.getApprovalStatus() != null) {

                weekStatus =
                        latestAttendance.getApprovalStatus();

            } else {

                weekStatus = "Draft";
            }
        }

        for (int i = 0; i < 7; i++) {

            LocalDate currentDate = monday.plusDays(i);

            Optional<Attendance> attendanceOpt =
                    attendanceRepository.findByUserAndDate(
                            user,
                            currentDate
                    );

            String recordedHours = "0hr 00min";

            List<AttendanceRegularization> dayRecords =
                    attendanceRegularizationRepository
                            .findByUserAndDate(
                                    user,
                                    currentDate
                            );

            int totalMinutes = 0;

            /* Card Hours */
            if (attendanceOpt.isPresent()
                    && attendanceOpt.get().getTotalHours() != null) {

                String totalHoursString =
                        attendanceOpt.get().getTotalHours();

                java.util.regex.Matcher matcher =
                        java.util.regex.Pattern
                                .compile("(\\d+)h\\s*(\\d+)m")
                                .matcher(totalHoursString);

                if (matcher.find()) {

                    totalMinutes +=
                            Integer.parseInt(matcher.group(1)) * 60;

                    totalMinutes +=
                            Integer.parseInt(matcher.group(2));
                }
            }

            /* Regularization Hours */
            for (AttendanceRegularization record : dayRecords) {

                String duration = record.getDuration();

                if (duration == null || duration.isBlank()) {
                    continue;
                }

                java.util.regex.Matcher matcher =
                        java.util.regex.Pattern
                                .compile("(\\d+)hr\\s*(\\d+)min")
                                .matcher(duration);

                if (matcher.find()) {

                    totalMinutes +=
                            Integer.parseInt(matcher.group(1)) * 60;

                    totalMinutes +=
                            Integer.parseInt(matcher.group(2));
                }
            }

            int hrs = totalMinutes / 60;
            int mins = totalMinutes % 60;

            recordedHours =
                    hrs + "hr " + mins + "min";

            int recordings =
                    attendanceRegularizationRepository
                            .findByUserAndDate(
                                    user,
                                    currentDate
                            )
                            .size();

            result.add(
            		new AttendanceDayDTO(
            		        currentDate.toString(),

            		        currentDate.format(
            		                DateTimeFormatter.ofPattern(
            		                        "EEEE, MMM dd, yyyy"
            		                )
            		        ),

            		        currentDate.getDayOfWeek() == DayOfWeek.SUNDAY
            		                ? "0hr 00min"
            		                : "9hr 00min",

            		        recordedHours,

            		        recordings,

            		        weekStatus
            		)
            );
            
        }

        return result;
    }
    
    public AttendanceRegularization saveAttendanceRegularization(
            User user,
            AttendanceRegularizationRequestDTO request) {
    	
    	LocalDate selectedDate =
    	        LocalDate.parse(
    	                request.getAttendanceDate()
    	        );

    	LocalDate currentMonday =
    	        LocalDate.now()
    	                 .with(DayOfWeek.MONDAY);

    	LocalDate currentSunday =
    	        currentMonday.plusDays(6);

    	if (selectedDate.isBefore(currentMonday)
    	        || selectedDate.isAfter(currentSunday)) {

    	    throw new RuntimeException(
    	            "Only current week attendance can be modified."
    	    );
    	}

        AttendanceRegularization regularization =
                new AttendanceRegularization();

        regularization.setUser(user);

        regularization.setDate(
                LocalDate.parse(
                        request.getAttendanceDate()
                )
        );

        regularization.setType(
                request.getTimeType()
        );
        
        String duration =
                request.getDuration();

        if (duration != null &&
            !duration.contains("hr")) {

            duration =
                duration + "hr 00min";
        }

        regularization.setDuration(duration);

        regularization.setReason(
                request.getTimeType()
        );

        regularization.setStatus("Pending");

        return attendanceRegularizationRepository
                .save(regularization);
    }
    public void submitCurrentWeekAttendance(
            String username) {

        User user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(
                                () -> new RuntimeException("User not found")
                        );

        LocalDate monday =
                LocalDate.now()
                         .with(DayOfWeek.MONDAY);

        String weekStart =
                monday.toString();

        String weekEnd =
                monday.plusDays(6)
                      .toString();

        List<Attendance> attendanceList =
                attendanceRepository
                        .findByUserAndWeekStartDateAndWeekEndDate(
                                user,
                                weekStart,
                                weekEnd
                        );

        Attendance attendance;

        if (!attendanceList.isEmpty()) {

            attendance = attendanceList.get(0);

        } else {

            attendance = new Attendance();

            attendance.setUser(user);
            attendance.setWeekStartDate(weekStart);
            attendance.setWeekEndDate(weekEnd);
        }
        
        int totalMinutes = 0;
        int presentDays = 0;
        double mondayHours = 0;
        double tuesdayHours = 0;
        double wednesdayHours = 0;
        double thursdayHours = 0;
        double fridayHours = 0;
        double saturdayHours = 0;

        for (int i = 0; i < 7; i++) {

            LocalDate currentDate =
                    monday.plusDays(i);

            List<AttendanceRegularization> records =
                    attendanceRegularizationRepository
                            .findByUserAndDate(
                                    user,
                                    currentDate
                            );

            if (!records.isEmpty()) {

                presentDays++;
            }
            int dayMinutes = 0;

            for (AttendanceRegularization record : records) {

                String duration =
                        record.getDuration();

                if (duration == null) {
                    continue;
                }
                java.util.regex.Matcher matcher =
                        java.util.regex.Pattern
                                .compile("(\\d+)hr\\s*(\\d+)min")
                                .matcher(duration);

                if (matcher.find()) {

                    int recordMinutes =
                            Integer.parseInt(matcher.group(1)) * 60
                            + Integer.parseInt(matcher.group(2));

                    totalMinutes += recordMinutes;
                    dayMinutes += recordMinutes;
                }
            }
            double dayHours = dayMinutes / 60.0;

            switch (i) {
                case 0 -> mondayHours = dayHours;
                case 1 -> tuesdayHours = dayHours;
                case 2 -> wednesdayHours = dayHours;
                case 3 -> thursdayHours = dayHours;
                case 4 -> fridayHours = dayHours;
                case 5 -> saturdayHours = dayHours;
                
            }
        }
        

        attendance.setPresentDays(
                presentDays
        );

        attendance.setAbsentDays(
                6 - presentDays
        );

        int hrs =
                totalMinutes / 60;

        int mins =
                totalMinutes % 60;

        attendance.setTotalHours(
                hrs + "hr " + mins + "min"
        );
        attendance.setMondayHours(mondayHours);
        attendance.setTuesdayHours(tuesdayHours);
        attendance.setWednesdayHours(wednesdayHours);
        attendance.setThursdayHours(thursdayHours);
        attendance.setFridayHours(fridayHours);
        attendance.setSaturdayHours(saturdayHours);
        attendance.setApprovalStatus("Pending");
        attendance.setSubmittedOn(LocalDate.now());

        attendanceRepository.save(attendance);
    }
    public void discardCurrentWeekAttendance(
            String username) {

        User user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(
                                () -> new RuntimeException("User not found")
                        );

        LocalDate monday =
                LocalDate.now()
                         .with(DayOfWeek.MONDAY);

        for (int i = 0; i < 7; i++) {

            LocalDate currentDate =
                    monday.plusDays(i);

            List<AttendanceRegularization> records =
                    attendanceRegularizationRepository
                            .findByUserAndDate(
                                    user,
                                    currentDate
                            );

            
        }

        List<Attendance> attendanceList =
                attendanceRepository
                        .findByUserAndWeekStartDateAndWeekEndDate(
                                user,
                                monday.toString(),
                                monday.plusDays(6).toString()
                        );

        if (!attendanceList.isEmpty()) {

            Attendance attendance =
                    attendanceList.get(0);

            attendance.setApprovalStatus("Draft");

            attendanceRepository.save(attendance);
        }
    }
}