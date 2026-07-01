package com.example.admindashboard.controller;

import com.example.admindashboard.model.DepartmentEntity;
import com.example.admindashboard.model.User;
import com.example.admindashboard.repository.DepartmentEntityRepository;
import com.example.admindashboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class SeniorHrOrganizationController {

    @Autowired
    private DepartmentEntityRepository departmentRepository;
    
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/senior_hr/organization")
    public String showOrganization(Model model) {
        if (departmentRepository.count() == 0) {
            seedMockData();
        }
        
        List<DepartmentEntity> departments = departmentRepository.findAll();
        List<User> users = userRepository.findAll();
        
        model.addAttribute("departments", departments);
        
        long activeCount = departments.stream().filter(d -> "Active".equalsIgnoreCase(d.getStatus())).count();
        int totalEmployees = departments.stream().mapToInt(d -> d.getEmployeesCount() != null ? d.getEmployeesCount() : 0).sum();
        long headsAssigned = departments.stream().filter(d -> d.getDepartmentHead() != null).count();
        
        model.addAttribute("totalDepartments", departments.size());
        model.addAttribute("activeDepartments", activeCount);
        model.addAttribute("totalEmployees", totalEmployees);
        model.addAttribute("departmentHeads", headsAssigned);
        
        return "senior_hr-organization";
    }
    
    private void seedMockData() {
        User dummyUser = userRepository.findAll().stream().findFirst().orElse(null);
        
        createDept("Top Management", dummyUser, 5, "Head Office", "Active");
        createDept("Human Resources", dummyUser, 24, "Head Office", "Active");
        createDept("Information Technology", dummyUser, 68, "Head Office, Remote", "Active");
        createDept("Finance", dummyUser, 34, "Head Office", "Active");
        createDept("Operations", dummyUser, 56, "Head Office, Branch", "Active");
        createDept("Customer Support", dummyUser, 22, "Head Office, Remote", "Inactive");
    }
    
    private void createDept(String name, User head, int count, String location, String status) {
        DepartmentEntity d = new DepartmentEntity();
        d.setDepartmentName(name);
        d.setDepartmentHead(head);
        d.setEmployeesCount(count);
        d.setLocations(location);
        d.setStatus(status);
        departmentRepository.save(d);
    }
}
