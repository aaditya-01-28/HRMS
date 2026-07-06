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

        // Need standard variables in context
        WebContext context = new WebContext(request, response, servletContext);
        
        // Add minimal model variables that might be required
        context.setVariable("pendingLeaves", Collections.emptyList());
        context.setVariable("pendingResignations", Collections.emptyList());
        context.setVariable("pendingReferrals", Collections.emptyList());
        context.setVariable("pendingAttendances", Collections.emptyList());
        context.setVariable("pendingTimesheets", Collections.emptyList());

        System.out.println("TEST_START_RENDER");
        try {
            String result = templateEngine.process("hr-workflow", context);
            System.out.println("TEST_SUCCESS: RENDERED OK");
        } catch (Exception e) {
            System.out.println("TEST_FAILURE: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
