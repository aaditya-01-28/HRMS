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
        try {
            Context ctx = new Context();
            ctx.setVariable("isItSupport", false);
            templateEngine.process("manager-workflow", ctx);
            System.out.println("TEMPLATE PARSED SUCCESSFULLY!");
        } catch (Exception e) {
            System.err.println("TEMPLATE PARSING FAILED:");
            e.printStackTrace();
        }
    }
}
