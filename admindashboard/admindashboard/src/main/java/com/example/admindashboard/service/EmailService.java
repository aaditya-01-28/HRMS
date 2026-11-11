package com.example.admindashboard.service;

import jakarta.mail.MessagingException;
import com.example.admindashboard.model.User;
import com.example.admindashboard.model.CompanyDetails;
import com.example.admindashboard.repository.CompanyDetailsRepository;
import com.example.admindashboard.repository.UserRepository;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.springframework.mail.SimpleMailMessage;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyDetailsRepository companyDetailsRepository;

    public String getCompanyName() {
        if (companyDetailsRepository != null) {
            return companyDetailsRepository.findAll().stream()
                    .findFirst()
                    .map(CompanyDetails::getCompanyName)
                    .filter(s -> s != null && !s.trim().isEmpty())
                    .orElse("Textora Technology");
        }
        return "Textora Technology";
    }

    // Pulls the email address from your application.properties
    @Value("${spring.mail.username}")
    private String systemEmail;

    @Async
    public void sendLeaveRequestToAdmin(String adminEmail, String employeeName, String employeeEmail, Map<String, Object> templateModel) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // 1. Setup Email Routing & Headers
            helper.setTo(adminEmail);
            helper.setFrom(systemEmail, "Textora HRMS"); // Looks professional in the inbox
            helper.setReplyTo(employeeEmail, employeeName);  // Clicking 'Reply' goes to the employee
            helper.setSubject("New Leave Request: " + employeeName);

            // 2. Inject Data into Thymeleaf
            Context thymeleafContext = new Context();
            thymeleafContext.setVariables(templateModel);

            // 3. Process the HTML Template
            // It will look for "leave-request-email.html" inside src/main/resources/templates/emails/
            String htmlBody = templateEngine.process("emails/leave-request-email", thymeleafContext);

            // 4. Attach the HTML to the email and Send!
            helper.setText(htmlBody, true); // 'true' means this is HTML, not plain text
            mailSender.send(message);

            System.out.println("✅ Background Email Sent Successfully to: " + adminEmail);

        } catch (MessagingException | UnsupportedEncodingException e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
        }
    }

    @Async
    public void sendOnboardingEmail(String toEmail, String fullName, String username, String tempPassword) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setFrom(systemEmail, "Textora HRMS");
            helper.setSubject("Welcome to Textora HRMS - Your Account Details");

            Context thymeleafContext = new Context();
            thymeleafContext.setVariable("companyName", getCompanyName());
            thymeleafContext.setVariable("candidateName", fullName);
            thymeleafContext.setVariable("empId", username);
            thymeleafContext.setVariable("officeEmail", toEmail);
            thymeleafContext.setVariable("password", tempPassword);

            String htmlBody = templateEngine.process("emails/onboarding-email", thymeleafContext);

            helper.setText(htmlBody, true);
            mailSender.send(message);

            System.out.println("✅ Onboarding Email Sent Successfully to: " + toEmail);
        } catch (MessagingException | UnsupportedEncodingException e) {
            System.err.println("❌ Failed to send onboarding email: " + e.getMessage());
        }
    }

    @Async
    public void sendTimesheetSubmissionToAdmin(String adminEmail, String employeeName, String employeeEmail, Map<String, Object> templateModel) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(adminEmail);
            helper.setFrom(systemEmail, "Textora HRMS");
            helper.setReplyTo(employeeEmail, employeeName);
            helper.setSubject("Timesheet Submitted: " + employeeName);

            Context thymeleafContext = new Context();
            thymeleafContext.setVariables(templateModel);

            // Looks for "timesheet-submission-email.html"
            String htmlBody = templateEngine.process("emails/timesheet-submission-email", thymeleafContext);

            helper.setText(htmlBody, true);
            mailSender.send(message);

            System.out.println("✅ Timesheet Email Sent Successfully to: " + adminEmail);

        } catch (Exception e) {
            System.err.println("❌ Failed to send timesheet email: " + e.getMessage());
        }
    }


    @Async
    public void sendTicketSubmissionToAdmin(String adminEmail, String employeeName, String employeeEmail, String ticketType, Map<String, Object> templateModel) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(adminEmail);
            helper.setFrom(systemEmail, "Textora IT Helpdesk");
            helper.setReplyTo(employeeEmail, employeeName);
            // Dynamic Subject: "New HARDWARE Ticket: John Doe"
            helper.setSubject("New " + ticketType.toUpperCase() + " Ticket: " + employeeName);

            Context thymeleafContext = new Context();
            thymeleafContext.setVariables(templateModel);

            // Looks for "ticket-submission-email.html"
            String htmlBody = templateEngine.process("emails/ticket-submission-email", thymeleafContext);

            helper.setText(htmlBody, true);
            mailSender.send(message);

            System.out.println("✅ IT Ticket Email Sent Successfully to: " + adminEmail);

        } catch (Exception e) {
            System.err.println("❌ Failed to send IT ticket email: " + e.getMessage());
        }
    }


    @Async
    public void sendAttendanceSubmissionToAdmin(String adminEmail, String employeeName, String employeeEmail, Map<String, Object> templateModel) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(adminEmail);
            helper.setFrom(systemEmail, "Textora HRMS");
            helper.setReplyTo(employeeEmail, employeeName);
            helper.setSubject("Attendance Regularization: " + employeeName);

            Context thymeleafContext = new Context();
            thymeleafContext.setVariables(templateModel);

            // Looks for "attendance-submission-email.html"
            String htmlBody = templateEngine.process("emails/attendance-submission-email", thymeleafContext);

            helper.setText(htmlBody, true);
            mailSender.send(message);

            System.out.println("✅ Attendance Email Sent Successfully to: " + adminEmail);

        } catch (Exception e) {
            System.err.println("❌ Failed to send attendance email: " + e.getMessage());
        }
    }


    @Async
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            org.springframework.mail.SimpleMailMessage message = new org.springframework.mail.SimpleMailMessage();
            message.setFrom(systemEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            System.out.println("✅ Simple Text Email Sent Successfully to: " + to);

        } catch (Exception e) {
            System.err.println("❌ Failed to send simple email: " + e.getMessage());
        }
    }


    @Async
    public void sendRequestStatusUpdateToEmployee(String employeeEmail, String employeeName, String requestType, String status, Map<String, Object> templateModel) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // 1. Setup Email Routing
            helper.setTo(employeeEmail);
            helper.setFrom(systemEmail, "Textora HRMS");

            // 2. Dynamic Subject Line (e.g., "Update on your Leave Request: Approved")
            helper.setSubject("Update on your " + requestType + " Request: " + status.toUpperCase());

            // 3. Inject Data into Thymeleaf
            Context thymeleafContext = new Context();
            thymeleafContext.setVariables(templateModel);

            // Pass the dynamic title and status directly to the template context
            thymeleafContext.setVariable("requestType", requestType);
            thymeleafContext.setVariable("status", status);

            // 4. Process the Universal HTML Template
            // We will create this single file next: src/main/resources/templates/emails/employee-status-update.html
            String htmlBody = templateEngine.process("emails/employee-status-update", thymeleafContext);

            // 5. Send!
            helper.setText(htmlBody, true);
            mailSender.send(message);

            System.out.println("✅ Status Update (" + status + ") Email Sent Successfully to Employee: " + employeeEmail);

        } catch (Exception e) {
            System.err.println("❌ Failed to send status update email to employee: " + e.getMessage());
        }
    }


    @Async
    public void sendMeetingInvite(String participantEmail, String participantName, String meetingTitle, Map<String, Object> templateModel) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(participantEmail);
            helper.setFrom(systemEmail, "Textora HRMS");
            helper.setSubject("Meeting Invite: " + meetingTitle);

            Context thymeleafContext = new Context();
            thymeleafContext.setVariables(templateModel);
            thymeleafContext.setVariable("participantName", participantName);

            String htmlBody = templateEngine.process("emails/meeting-invite", thymeleafContext);

            helper.setText(htmlBody, true);
            mailSender.send(message);

            System.out.println("✅ Meeting Invite Email Sent Successfully to: " + participantEmail);

        } catch (Exception e) {
            System.err.println("❌ Failed to send meeting invite email to " + participantEmail + ": " + e.getMessage());
        }
    }

    @Async
    public void sendPasswordResetEmail(String to, String resetUrl) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setFrom(systemEmail, "Textora Security");
            helper.setSubject("Textora Technology - Password Reset Request");

            String htmlContent = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e2e8f0; border-radius: 8px;'>"
                + "<div style='text-align: center; margin-bottom: 20px;'>"
                + "  <h2 style='color: #1e3a8a; margin: 0;'>Password Reset Request</h2>"
                + "</div>"
                + "<p style='color: #334155; font-size: 16px;'>Hello,</p>"
                + "<p style='color: #334155; font-size: 16px;'>We received a request to reset the password for your Textora Technology account.</p>"
                + "<p style='color: #334155; font-size: 16px;'>Click the secure button below to set a new password. This link will safely expire in 1 hour.</p>"
                + "<div style='text-align: center; margin: 35px 0;'>"
                + "  <a href='" + resetUrl + "' style='background-color: #2563eb; color: white; padding: 14px 28px; text-decoration: none; border-radius: 6px; font-weight: bold; font-size: 16px; display: inline-block;'>Reset My Password</a>"
                + "</div>"
                + "<p style='color: #64748b; font-size: 14px;'>If you didn't request a password reset, you can safely ignore this email. Your account remains secure.</p>"
                + "<hr style='border: none; border-top: 1px solid #e2e8f0; margin: 30px 0;'/>"
                + "<p style='color: #94a3b8; font-size: 12px; text-align: center;'>Best regards,<br>The Textora Technologies Team</p>"
                + "</div>";

            helper.setText(htmlContent, true);
            mailSender.send(message);

            System.out.println("✅ Password Reset Email Sent Successfully to: " + to);

        } catch (Exception e) {
            System.err.println("❌ Failed to send password reset email to " + to + ": " + e.getMessage());
        }
    }

    // =========================================================================
    // ADDED: DYNAMIC SEAMLESS ROUTING FOR SENDING EMPLOYEE TICKETS
    // =========================================================================
    @Async
    public void sendHelpdeskNotifications(String departmentCode, String employeeName, String employeeEmail, String formSubject, Map<String, Object> templateModel) {
        try {
            String cleanDept = (departmentCode != null) ? departmentCode.trim().toUpperCase() : "IT";

            // 1. Resolve Department Head Email via dynamic environment system variables (Render / System OS)
            String deptHeadEmail = null;

            switch (cleanDept) {

                case "IT":
                    deptHeadEmail = userRepository.findAll().stream()
                            .filter(u -> u.getRole() != null)
                            .filter(u -> "IT_SUPPORT".equalsIgnoreCase(u.getRole().getRoleName()))
                            .filter(u -> "ACTIVE".equalsIgnoreCase(u.getStatus()))
                            .map(User::getEmail)
                            .findFirst()
                            .orElse(null);
                    break;

                case "HR":
                    deptHeadEmail = userRepository.findAll().stream()
                            .filter(u -> u.getRole() != null)
                            .filter(u -> "HR_MANAGER".equalsIgnoreCase(u.getRole().getRoleName()))
                            .filter(u -> "ACTIVE".equalsIgnoreCase(u.getStatus()))
                            .map(User::getEmail)
                            .findFirst()
                            .orElse(null);
                    break;

                case "PROJECT":
                    deptHeadEmail = userRepository.findAll().stream()
                            .filter(u -> u.getRole() != null)
                            .filter(u -> "PROJECT_MANAGER".equalsIgnoreCase(u.getRole().getRoleName()))
                            .filter(u -> "ACTIVE".equalsIgnoreCase(u.getStatus()))
                            .map(User::getEmail)
                            .findFirst()
                            .orElse(null);
                    break;
            }
            if (deptHeadEmail == null || deptHeadEmail.isBlank()) {
                deptHeadEmail = systemEmail;
            }

            // 2. Resolve Admin Target utilizing company's active properties registration variable
            String adminEmail = this.systemEmail;

            // 3. Construct HTML Payload using the shared template layout
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(systemEmail, "Textora Helpdesk");
            helper.setReplyTo(employeeEmail, employeeName);
            helper.setSubject("New Helpdesk Submission [" + cleanDept + "]: " + formSubject);

            Context thymeleafContext = new Context();
            thymeleafContext.setVariables(templateModel);
            String htmlBody = templateEngine.process("emails/ticket-submission-email", thymeleafContext);
            helper.setText(htmlBody, true);

         // Send to Department Head
            MimeMessage deptMessage = mailSender.createMimeMessage();
            MimeMessageHelper deptHelper = new MimeMessageHelper(deptMessage, true, "UTF-8");

            deptHelper.setFrom(systemEmail, "Textora Helpdesk");
            deptHelper.setReplyTo(employeeEmail, employeeName);
            deptHelper.setTo(deptHeadEmail);
            deptHelper.setSubject("New Helpdesk Submission [" + cleanDept + "]: " + formSubject);
            deptHelper.setText(htmlBody, true);

            mailSender.send(deptMessage);

            // Send to Admin
            MimeMessage adminMessage = mailSender.createMimeMessage();
            MimeMessageHelper adminHelper = new MimeMessageHelper(adminMessage, true, "UTF-8");

            adminHelper.setFrom(systemEmail, "Textora Helpdesk");
            adminHelper.setReplyTo(employeeEmail, employeeName);
            adminHelper.setTo(adminEmail);
            adminHelper.setSubject("New Helpdesk Submission [" + cleanDept + "]: " + formSubject);
            adminHelper.setText(htmlBody, true);

            mailSender.send(adminMessage);

            System.out.println("✅ Helpdesk Notification successfully pushed to Admin & Head of " + cleanDept);

        } catch (Exception e) {
            System.err.println("❌ Helpdesk Notification Delivery Error: " + e.getMessage());
        }
    }

    @Async
    public void sendReferralEmailToCandidate(String candidateEmail, String candidateName, String employeeName, Map<String, Object> templateModel) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(candidateEmail);
            helper.setFrom(systemEmail, "Textora HRMS");
            helper.setSubject(employeeName + " referred you for a role at Textora Technology!");

            Context thymeleafContext = new Context();
            thymeleafContext.setVariables(templateModel);

            String htmlBody = templateEngine.process("emails/referral-candidate-email", thymeleafContext);

            helper.setText(htmlBody, true);
            mailSender.send(message);

            System.out.println("✅ Candidate Referral Email Sent Successfully to: " + candidateEmail);

        } catch (Exception e) {
            System.err.println("❌ Candidate Referral Email Delivery Error: " + e.getMessage());
        }
    }

    @Async
    public void sendOnboardingEmail(String personalEmail, String candidateName, String empId, String password, String officeEmail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(personalEmail);
            helper.setFrom(systemEmail, "Textora HRMS");
            helper.setSubject("Welcome to Textora Technology! Your Onboarding Details");

            Context thymeleafContext = new Context();
            thymeleafContext.setVariable("candidateName", candidateName);
            thymeleafContext.setVariable("empId", empId);
            thymeleafContext.setVariable("password", password);
            thymeleafContext.setVariable("officeEmail", officeEmail);
            thymeleafContext.setVariable("companyName", getCompanyName());

            String htmlBody = templateEngine.process("emails/onboarding-email", thymeleafContext);

            helper.setText(htmlBody, true);
            mailSender.send(message);

            System.out.println("✅ Onboarding Email Sent Successfully to: " + personalEmail);

        } catch (Exception e) {
            System.err.println("❌ Onboarding Email Delivery Error: " + e.getMessage());
        }
    }

    @Async
    public void sendGenericEmail(String toEmail, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setFrom(systemEmail != null ? systemEmail : "no-reply@textoratechnologies.com", "Textora HRMS");
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("✅ Generic Email Sent Successfully to: " + toEmail);
        } catch (Exception e) {
            System.err.println("❌ Generic Email Delivery Error: " + e.getMessage());
        }
    }
}