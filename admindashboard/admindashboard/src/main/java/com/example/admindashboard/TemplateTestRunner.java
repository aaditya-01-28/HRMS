package com.example.admindashboard;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class TemplateTestRunner implements CommandLineRunner {

    private final TemplateEngine templateEngine;

    public TemplateTestRunner(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("TESTING TEMPLATE PARSING...");
        String[] templates = {"manager-workflow", "hr-workflow", "my-approvals"};
        for (String t : templates) {
            try {
                Context ctx = new Context();
                ctx.setVariable("isItSupport", false);
                ctx.setVariable("pendingLeaves", new java.util.ArrayList<>());
                ctx.setVariable("pendingAttendances", new java.util.ArrayList<>());
                ctx.setVariable("pendingTimesheets", new java.util.ArrayList<>());
                ctx.setVariable("pendingResignations", new java.util.ArrayList<>());
                ctx.setVariable("pendingReferrals", new java.util.ArrayList<>());
                ctx.setVariable("allTimesheets", new java.util.ArrayList<>());
                ctx.setVariable("allServiceRequests", new java.util.ArrayList<>());
                ctx.setVariable("accountsTickets", new java.util.ArrayList<>());
                
                templateEngine.process(t, ctx);
                System.out.println("TEMPLATE " + t + " PARSED SUCCESSFULLY!");
            } catch (Exception e) {
                System.err.println("TEMPLATE PARSING FAILED FOR " + t + ":");
                e.printStackTrace();
            }
        }
    }
}
