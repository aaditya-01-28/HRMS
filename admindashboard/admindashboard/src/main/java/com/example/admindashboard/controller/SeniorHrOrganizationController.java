package com.example.admindashboard.controller;

import com.example.admindashboard.model.DepartmentEntity;
import com.example.admindashboard.model.User;
import com.example.admindashboard.repository.DepartmentEntityRepository;
import com.example.admindashboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;
import com.example.admindashboard.dto.HierarchyNodeDTO;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;

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
    
    @GetMapping("/senior_hr/api/organization/hierarchy")
    @ResponseBody
    public ResponseEntity<List<HierarchyNodeDTO>> getOrganizationHierarchy() {
        List<User> allUsers = userRepository.findAll();
        List<DepartmentEntity> allDepts = departmentRepository.findAll();
        Map<Long, String> deptMap = allDepts.stream().collect(Collectors.toMap(DepartmentEntity::getId, DepartmentEntity::getDepartmentName));
        
        List<HierarchyNodeDTO> nodes = new ArrayList<>();
        for (User u : allUsers) {
            HierarchyNodeDTO dto = new HierarchyNodeDTO();
            dto.setId(u.getId());
            dto.setName(u.getFullName());
            dto.setDesignation(u.getDesignation() != null ? u.getDesignation() : (u.getRole() != null ? u.getRole().getRoleName() : "Employee"));
            dto.setEmail(u.getEmail());
            dto.setProfileImage(u.getProfileImage());
            
            if (u.getDepartmentId() != null && deptMap.containsKey(u.getDepartmentId())) {
                dto.setDepartment(deptMap.get(u.getDepartmentId()));
            } else {
                dto.setDepartment("N/A");
            }
            
            if (u.getEmployeeProfile() != null && u.getEmployeeProfile().getMobileNumber() != null) {
                dto.setPhone(u.getEmployeeProfile().getMobileNumber());
            } else {
                dto.setPhone("-");
            }
            
            nodes.add(dto);
        }
        
        // Build Tree structure
        List<HierarchyNodeDTO> roots = new ArrayList<>();
        for (User u : allUsers) {
            HierarchyNodeDTO node = findNodeById(nodes, u.getId());
            if (u.getManager() == null) {
                roots.add(node);
            } else {
                HierarchyNodeDTO parent = findNodeById(nodes, u.getManager().getId());
                if (parent != null) {
                    parent.getChildren().add(node);
                } else {
                    roots.add(node); // Fallback to root if parent missing
                }
            }
        }
        
        // Compute sizes
        for (HierarchyNodeDTO root : roots) {
            computeSizes(root);
        }
        
        return ResponseEntity.ok(roots);
    }
    
    private HierarchyNodeDTO findNodeById(List<HierarchyNodeDTO> nodes, Long id) {
        return nodes.stream().filter(n -> n.getId().equals(id)).findFirst().orElse(null);
    }
    
    private int computeSizes(HierarchyNodeDTO node) {
        int size = node.getChildren().size();
        node.setDirectReports(size);
        for (HierarchyNodeDTO child : node.getChildren()) {
            size += computeSizes(child);
        }
        node.setTeamSize(size);
        return size;
    }
    
    @PostMapping("/senior_hr/api/organization/assign-manager")
    @ResponseBody
    public ResponseEntity<?> assignManager(@RequestBody Map<String, Long> payload) {
        Long employeeId = payload.get("employeeId");
        Long managerId = payload.get("managerId");
        
        User emp = userRepository.findById(employeeId).orElse(null);
        User mgr = managerId != null ? userRepository.findById(managerId).orElse(null) : null;
        
        if (emp != null) {
            emp.setManager(mgr);
            userRepository.save(emp);
            return ResponseEntity.ok(Map.of("success", true));
        }
        return ResponseEntity.badRequest().build();
    }
    
    @PostMapping("/senior_hr/api/organization/transfer-employee")
    @ResponseBody
    public ResponseEntity<?> transferEmployee(@RequestBody Map<String, Long> payload) {
        Long employeeId = payload.get("employeeId");
        Long departmentId = payload.get("departmentId");
        
        User emp = userRepository.findById(employeeId).orElse(null);
        
        if (emp != null) {
            emp.setDepartmentId(departmentId);
            userRepository.save(emp);
            return ResponseEntity.ok(Map.of("success", true));
        }
        return ResponseEntity.badRequest().build();
    }
}
