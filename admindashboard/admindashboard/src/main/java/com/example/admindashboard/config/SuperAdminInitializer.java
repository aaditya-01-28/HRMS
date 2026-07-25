package com.example.admindashboard.config;

import com.example.admindashboard.model.EmployeeProfile;
import com.example.admindashboard.model.Role;
import com.example.admindashboard.model.User;
import com.example.admindashboard.repository.RoleRepository;
import com.example.admindashboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Optional;

@Component
public class SuperAdminInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @PostConstruct
    public void initSuperAdmin() {
        try {
            // Ensure Super Admin role exists
            Role superAdminRole = roleRepository.findByRoleName("SUPER_ADMIN").orElseGet(() -> {
                Role role = new Role();
                role.setRoleName("SUPER_ADMIN");
                role.setDescription("Super Administrator");
                return roleRepository.save(role);
            });

            // Ensure SADM001 exists
            if (!userRepository.existsByUsername("SADM001")) {
                User sadm = new User();
                sadm.setUsername("SADM001");
                sadm.setPassword("{noop}welcome123");
                sadm.setFullName("Rohit Singh");
                sadm.setEmail("s.admin@wcg.com");
                sadm.setRole(superAdminRole);
                sadm.setStatus("ACTIVE");
                
                EmployeeProfile profile = new EmployeeProfile();
                profile.setDesignation("Super Admin");
                profile.setExperience("5");
                profile.setJoiningDate(LocalDate.of(2025, 5, 6)); // 06-05-2025
                profile.setMobileNumber("9656556415");
                profile.setUser(sadm);
                
                sadm.setEmployeeProfile(profile);
                
                userRepository.save(sadm);
                System.out.println("✅ Seeded Super Admin user: SADM001 with password: welcome123");
            } else {
                System.out.println("✅ Super Admin user SADM001 already exists in the database.");
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to seed Super Admin: " + e.getMessage());
        }
    }
}
