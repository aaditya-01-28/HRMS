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
}
