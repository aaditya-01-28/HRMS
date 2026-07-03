package com.example.admindashboard;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

public class ThymeleafDebug {
    public static void main(String[] args) {
        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix("src/main/resources/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        
        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(resolver);
        
        try {
            System.out.println("Starting Thymeleaf parse...");
            engine.process("senior_hr-myspace", new Context());
            System.out.println("Parse SUCCESS");
        } catch (Exception e) {
            System.out.println("Parse FAILED");
            e.printStackTrace();
        }
    }
}
