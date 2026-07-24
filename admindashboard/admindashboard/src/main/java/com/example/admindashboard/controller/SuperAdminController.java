package com.example.admindashboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.access.prepost.PreAuthorize;

@Controller
@RequestMapping("/superadmin")
@PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_SUPER ADMIN') or hasAuthority('SUPER_ADMIN')")
public class SuperAdminController {

    @GetMapping("/company")
    public String companySettings() {
        return "superadmin/settings-company";
    }

    @GetMapping("/statutory")
    public String statutorySettings() {
        return "superadmin/settings-statutory";
    }

}
