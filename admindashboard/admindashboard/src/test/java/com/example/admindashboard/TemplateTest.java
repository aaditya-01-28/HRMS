package com.example.admindashboard;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;
@SpringBootTest
public class TemplateTest {
    @Autowired
    private SpringTemplateEngine templateEngine;
    @Test
    public void testLmsTemplate() {
        try {
            Context context = new Context();
            context.setVariable("leaveTypes", java.util.Collections.emptyList());
            context.setVariable("holidays", java.util.Collections.emptyList());
            context.setVariable("compOffEntries", java.util.Collections.emptyList());
            context.setVariable("todayLeaves", java.util.Collections.emptyList());
            String result = templateEngine.process("senior_hr-lms", context);
            System.out.println("TEMPLATE PARSED SUCCESSFULLY. LENGTH: " + result.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
