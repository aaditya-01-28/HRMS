package com.example.admindashboard;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.WebContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.StringWriter;
import java.util.Collections;

@SpringBootTest
public class ThymeleafSyntaxTest {

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testHrWorkflowSyntax() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/senior_hr/workflow");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockServletContext servletContext = new MockServletContext();

        JakartaServletWebApplication application = JakartaServletWebApplication.buildApplication(servletContext);
        IWebExchange exchange = application.buildExchange(request, response);

        // Need standard variables in context
        WebContext context = new WebContext(exchange);
        
        // Add minimal model variables that might be required
        context.setVariable("pendingLeaves", Collections.emptyList());
        context.setVariable("pendingResignations", Collections.emptyList());
        context.setVariable("pendingReferrals", Collections.emptyList());
        context.setVariable("pendingAttendances", Collections.emptyList());
        context.setVariable("pendingTimesheets", Collections.emptyList());
        context.setVariable("isItSupport", false);

        System.out.println("TEST_START_RENDER");
        try {
            String result = templateEngine.process("hr-workflow", context);
            System.out.println("TEST_SUCCESS: RENDERED OK");
        } catch (Exception e) {
            System.out.println("TEST_FAILURE: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void testSeniorHrEmployeeSyntax() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/senior_hr/employee");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockServletContext servletContext = new MockServletContext();

        JakartaServletWebApplication application = JakartaServletWebApplication.buildApplication(servletContext);
        IWebExchange exchange = application.buildExchange(request, response);

        WebContext context = new WebContext(exchange);
        
        context.setVariable("candidates", Collections.emptyList());
        context.setVariable("totalEmployees", 0L);
        context.setVariable("activeEmployees", 0L);
        context.setVariable("noticePeriodEmployees", 0L);
        context.setVariable("offboardedEmployees", 0L);
        context.setVariable("onboardingEmployees", 0L);
        context.setVariable("deptCounts", Collections.emptyMap());
        context.setVariable("engPct", 0L);
        context.setVariable("prodPct", 0L);
        context.setVariable("desPct", 0L);
        context.setVariable("mktPct", 0L);
        context.setVariable("salesPct", 0L);
        context.setVariable("hrPct", 0L);
        context.setVariable("finPct", 0L);
        context.setVariable("othersPct", 0L);
        context.setVariable("allUsers", Collections.emptyList());

        System.out.println("TEST_START_RENDER_EMPLOYEE");
        try {
            String result = templateEngine.process("senior_hr-employee", context);
            System.out.println("TEST_SUCCESS: RENDERED EMPLOYEE OK");
        } catch (Exception e) {
            System.out.println("TEST_FAILURE EMPLOYEE: " + e.getMessage());
            e.printStackTrace();
            org.junit.jupiter.api.Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testSeniorManagerLeaveAttendanceSyntax() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/senior_manager/leave_attendance");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockServletContext servletContext = new MockServletContext();

        JakartaServletWebApplication application = JakartaServletWebApplication.buildApplication(servletContext);
        IWebExchange exchange = application.buildExchange(request, response);

        WebContext context = new WebContext(exchange);
        
        context.setVariable("activeTab", "leave_calendar");
        context.setVariable("casualLeaveCount", 18);
        context.setVariable("sickLeaveCount", 12);
        context.setVariable("earnedLeaveCount", 10);
        context.setVariable("compOffCount", 5);
        context.setVariable("entitledLeaves", 124);
        context.setVariable("usedLeaves", 45);
        context.setVariable("pendingLeaves", 5);
        context.setVariable("remainingLeaves", 79);
        context.setVariable("calendarEvents", Collections.emptyList());
        context.setVariable("attendanceRecords", Collections.emptyList());
        context.setVariable("attendanceLabels", Collections.emptyList());
        context.setVariable("presentTrend", Collections.emptyList());
        context.setVariable("deptAttendanceLabels", Collections.emptyList());
        context.setVariable("deptAttendanceData", Collections.emptyList());
        context.setVariable("avgWorkingHours", "8h 32m");

        System.out.println("TEST_START_RENDER_SM_LEAVE");
        try {
            String result = templateEngine.process("senior_manager-leave_attendance", context);
            System.out.println("TEST_SUCCESS: RENDERED SM LEAVE OK");
        } catch (Exception e) {
            System.out.println("TEST_FAILURE SM LEAVE: " + e.getMessage());
            e.printStackTrace();
            org.junit.jupiter.api.Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testSeniorManagerProjectWorkSyntax() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/senior_manager/project_work");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockServletContext servletContext = new MockServletContext();

        JakartaServletWebApplication application = JakartaServletWebApplication.buildApplication(servletContext);
        IWebExchange exchange = application.buildExchange(request, response);

        WebContext context = new WebContext(exchange);
        
        context.setVariable("totalMembers", 10);
        context.setVariable("activeCount", 8);
        context.setVariable("benchCount", 1);
        context.setVariable("exitedCount", 1);
        context.setVariable("projectsList", Collections.emptyList());
        context.setVariable("activeEmployees", Collections.emptyList());
        context.setVariable("teamworkRecords", Collections.emptyList());
        context.setVariable("timesheetRecords", Collections.emptyList());

        System.out.println("TEST_START_RENDER_SM_PROJECT");
        try {
            String result = templateEngine.process("senior_manager-project_work", context);
            System.out.println("TEST_SUCCESS: RENDERED SM PROJECT OK");
        } catch (Exception e) {
            System.out.println("TEST_FAILURE SM PROJECT: " + e.getMessage());
            e.printStackTrace();
            org.junit.jupiter.api.Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testSeniorManagerPerformanceSyntax() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/senior_manager/performance");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockServletContext servletContext = new MockServletContext();

        JakartaServletWebApplication application = JakartaServletWebApplication.buildApplication(servletContext);
        IWebExchange exchange = application.buildExchange(request, response);

        WebContext context = new WebContext(exchange);
        
        context.setVariable("activeTab", "overview");
        context.setVariable("totalMembers", 10);
        context.setVariable("completedCount", 5);
        context.setVariable("inProgressCount", 3);
        context.setVariable("pendingCount", 1);
        context.setVariable("overdueCount", 1);
        context.setVariable("avgRating", 4.2);
        context.setVariable("outstanding", 2);
        context.setVariable("exceeds", 3);
        context.setVariable("meets", 3);
        context.setVariable("below", 1);
        context.setVariable("unsatisfactory", 1);
        context.setVariable("bucket1", 1);
        context.setVariable("bucket2", 1);
        context.setVariable("bucket3", 3);
        context.setVariable("bucket4", 3);
        context.setVariable("bucket5", 2);
        context.setVariable("reviewsList", Collections.emptyList());
        context.setVariable("departments", Collections.emptySet());

        System.out.println("TEST_START_RENDER_SM_PERFORMANCE");
        try {
            String result = templateEngine.process("senior_manager-performance", context);
            System.out.println("TEST_SUCCESS: RENDERED SM PERFORMANCE OK");
        } catch (Exception e) {
            System.out.println("TEST_FAILURE SM PERFORMANCE: " + e.getMessage());
            e.printStackTrace();
            org.junit.jupiter.api.Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testSeniorManagerExpensesSyntax() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/senior_manager/expenses");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockServletContext servletContext = new MockServletContext();

        JakartaServletWebApplication application = JakartaServletWebApplication.buildApplication(servletContext);
        IWebExchange exchange = application.buildExchange(request, response);

        WebContext context = new WebContext(exchange);
        
        context.setVariable("activeTab", "approvals");
        context.setVariable("departments", Collections.emptySet());
        context.setVariable("approvalsList", Collections.emptyList());
        context.setVariable("reimbursementsList", Collections.emptyList());
        context.setVariable("budgetRequestsList", Collections.emptyList());
        context.setVariable("assignableRequestsList", Collections.emptyList());
        context.setVariable("allUsersList", Collections.emptyList());
        context.setVariable("reimApprovedCount", 0);
        context.setVariable("reimPendingCount", 0);
        context.setVariable("reimRejectedCount", 0);
        context.setVariable("budgetApprovedCount", 0);
        context.setVariable("budgetPendingCount", 0);
        context.setVariable("budgetRejectedCount", 0);

        System.out.println("TEST_START_RENDER_SM_EXPENSES");
        try {
            String result = templateEngine.process("senior_manager-expenses", context);
            System.out.println("TEST_SUCCESS: RENDERED SM EXPENSES OK");
        } catch (Exception e) {
            System.out.println("TEST_FAILURE SM EXPENSES: " + e.getMessage());
            e.printStackTrace();
            org.junit.jupiter.api.Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testSeniorManagerReportsSyntax() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/senior_manager/reports");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockServletContext servletContext = new MockServletContext();

        JakartaServletWebApplication application = JakartaServletWebApplication.buildApplication(servletContext);
        IWebExchange exchange = application.buildExchange(request, response);

        WebContext context = new WebContext(exchange);
        
        context.setVariable("activeTab", "performance");
        context.setVariable("departments", Collections.emptySet());
        context.setVariable("performanceList", Collections.emptyList());
        context.setVariable("attendanceList", Collections.emptyList());
        context.setVariable("leaveTrendsList", Collections.emptyList());
        context.setVariable("projectMetricsList", Collections.emptyList());
        context.setVariable("reportCasualCount", 0);
        context.setVariable("reportSickCount", 0);
        context.setVariable("reportPrivilegeCount", 0);
        context.setVariable("reportWfhCount", 0);
        context.setVariable("reportOtherCount", 0);
        context.setVariable("reportOnTrackCount", 0);
        context.setVariable("reportAtRiskCount", 0);
        context.setVariable("reportDelayedCount", 0);
        context.setVariable("progressBucket1", 0);
        context.setVariable("progressBucket2", 0);
        context.setVariable("progressBucket3", 0);
        context.setVariable("progressBucket4", 0);

        System.out.println("TEST_START_RENDER_SM_REPORTS");
        try {
            String result = templateEngine.process("senior_manager-reports", context);
            System.out.println("TEST_SUCCESS: RENDERED SM RPORTS OK");
        } catch (Exception e) {
            System.out.println("TEST_FAILURE SM REPORTS: " + e.getMessage());
            e.printStackTrace();
            org.junit.jupiter.api.Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testSeniorItMyspaceSyntax() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/senior_it/my_space");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockServletContext servletContext = new MockServletContext();

        JakartaServletWebApplication application = JakartaServletWebApplication.buildApplication(servletContext);
        IWebExchange exchange = application.buildExchange(request, response);

        WebContext context = new WebContext(exchange);
        
        // Mock User
        com.example.admindashboard.model.User currentUser = new com.example.admindashboard.model.User();
        currentUser.setUsername("EMP0001");
        currentUser.setFullName("Rajesh Kumar");
        com.example.admindashboard.model.EmployeeProfile profile = new com.example.admindashboard.model.EmployeeProfile();
        profile.setDepartment("IT");
        profile.setDesignation("Senior IT Head");
        currentUser.setEmployeeProfile(profile);
        
        context.setVariable("currentUser", currentUser);
        context.setVariable("activeTab", "dashboard");
        context.setVariable("totalTicketsCount", 10L);
        context.setVariable("pendingTicketsCount", 3L);
        context.setVariable("inProgressTicketsCount", 2L);
        context.setVariable("resolvedTodayCount", 5L);
        context.setVariable("avgResolutionTime", "4.2 hrs");
        context.setVariable("softwareCount", 4L);
        context.setVariable("hardwareCount", 3L);
        context.setVariable("accessCount", 2L);
        context.setVariable("networkCount", 1L);
        context.setVariable("softwarePercent", 40L);
        context.setVariable("hardwarePercent", 30L);
        context.setVariable("accessPercent", 20L);
        context.setVariable("networkPercent", 10L);
        context.setVariable("recentTickets", Collections.emptyList());
        
        // Users list
        context.setVariable("users", Collections.singletonList(currentUser));
        context.setVariable("roles", Collections.emptyList());
        context.setVariable("permissions", Collections.emptyList());
        context.setVariable("assets", Collections.emptyList());
        context.setVariable("activeAssetsCount", 10L);
        context.setVariable("maintenanceAssetsCount", 2L);
        context.setVariable("disposedAssetsCount", 1L);
        context.setVariable("securityPolicies", Collections.emptyList());
        context.setVariable("backupSchedules", Collections.emptyList());
        context.setVariable("broadcasts", Collections.emptyList());
        context.setVariable("departments", Collections.singletonList("IT"));

        System.out.println("TEST_START_RENDER_IT_MYSPACE");
        try {
            String result = templateEngine.process("senior_it-myspace", context);
            System.out.println("TEST_SUCCESS: RENDERED IT MYSPACE OK");
        } catch (Exception e) {
            System.out.println("TEST_FAILURE IT MYSPACE: " + e.getMessage());
            e.printStackTrace();
            org.junit.jupiter.api.Assertions.fail(e.getMessage());
        }
    }
}
