package com.example.admindashboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {
    @GetMapping("/test-dashboard")
    public String testDashboard() {
        return "my-rides/dashboard";
    }
}
