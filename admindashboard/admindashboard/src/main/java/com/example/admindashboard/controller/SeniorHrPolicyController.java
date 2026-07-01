package com.example.admindashboard.controller;

import com.example.admindashboard.model.CompanyPolicy;
import com.example.admindashboard.model.StatutoryCompliance;
import com.example.admindashboard.model.UpcomingAudit;
import com.example.admindashboard.model.User;
import com.example.admindashboard.repository.CompanyPolicyRepository;
import com.example.admindashboard.repository.StatutoryComplianceRepository;
import com.example.admindashboard.repository.UpcomingAuditRepository;
import com.example.admindashboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class SeniorHrPolicyController {

    @Autowired
    private CompanyPolicyRepository policyRepository;
    
    @Autowired
    private StatutoryComplianceRepository complianceRepository;
    
    @Autowired
    private UpcomingAuditRepository auditRepository;
    
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/senior_hr/policies")
    public String showPolicies(Model model) {
        // Seed initial mock data if empty
        if (policyRepository.count() == 0) {
            seedMockData();
        }
        
        List<CompanyPolicy> policies = policyRepository.findAll();
        List<StatutoryCompliance> compliances = complianceRepository.findAll();
        List<UpcomingAudit> audits = auditRepository.findAll();
        
        model.addAttribute("policies", policies);
        model.addAttribute("compliances", compliances);
        model.addAttribute("audits", audits);
        
        long activePoliciesCount = policies.stream().filter(p -> "Active".equalsIgnoreCase(p.getStatus())).count();
        long draftPoliciesCount = policies.stream().filter(p -> "Draft".equalsIgnoreCase(p.getStatus())).count();
        long archivedPoliciesCount = policies.stream().filter(p -> "Archived".equalsIgnoreCase(p.getStatus())).count();
        
        model.addAttribute("totalPolicies", policies.size());
        model.addAttribute("activePolicies", activePoliciesCount);
        model.addAttribute("draftPolicies", draftPoliciesCount);
        model.addAttribute("archivedPolicies", archivedPoliciesCount);
        
        long compliantCount = compliances.stream().filter(c -> "Compliant".equalsIgnoreCase(c.getStatus())).count();
        long dueSoonCount = compliances.stream().filter(c -> "Due Soon".equalsIgnoreCase(c.getStatus())).count();
        long overdueCount = compliances.stream().filter(c -> "Overdue".equalsIgnoreCase(c.getStatus())).count();
        
        model.addAttribute("totalCompliances", compliances.size());
        model.addAttribute("compliantCount", compliantCount);
        model.addAttribute("dueSoonCount", dueSoonCount);
        model.addAttribute("overdueCount", overdueCount);
        
        return "senior_hr-policies";
    }
    
    private void seedMockData() {
        CompanyPolicy p1 = new CompanyPolicy();
        p1.setPolicyTitle("Code of Conduct Policy");
        p1.setPolicyCategory("Workplace");
        p1.setEffectiveFrom(LocalDate.of(2025, 1, 1));
        p1.setStatus("Active");
        p1.setLastUpdated(LocalDateTime.of(2026, 6, 20, 10, 0));
        policyRepository.save(p1);
        
        CompanyPolicy p2 = new CompanyPolicy();
        p2.setPolicyTitle("Leave Policy");
        p2.setPolicyCategory("HR Policy");
        p2.setEffectiveFrom(LocalDate.of(2025, 4, 1));
        p2.setStatus("Active");
        p2.setLastUpdated(LocalDateTime.of(2026, 5, 18, 10, 0));
        policyRepository.save(p2);
        
        CompanyPolicy p3 = new CompanyPolicy();
        p3.setPolicyTitle("Data Privacy Policy");
        p3.setPolicyCategory("IT & Security");
        p3.setEffectiveFrom(LocalDate.of(2025, 2, 1));
        p3.setStatus("Draft");
        p3.setLastUpdated(LocalDateTime.of(2026, 5, 10, 10, 0));
        policyRepository.save(p3);
        
        CompanyPolicy p4 = new CompanyPolicy();
        p4.setPolicyTitle("Remote Work Policy");
        p4.setPolicyCategory("HR Policy");
        p4.setEffectiveFrom(LocalDate.of(2025, 6, 1));
        p4.setStatus("Active");
        p4.setLastUpdated(LocalDateTime.of(2026, 4, 25, 10, 0));
        policyRepository.save(p4);
        
        User dummyUser = userRepository.findAll().stream().findFirst().orElse(null);
        
        StatutoryCompliance c1 = new StatutoryCompliance();
        c1.setComplianceType("PF (Provident Fund)");
        c1.setApplicableTo("All Employees");
        c1.setFrequency("Monthly");
        c1.setNextDueDate(LocalDate.of(2026, 6, 15));
        c1.setStatus("Compliant");
        c1.setLastCompletedOn(LocalDate.of(2026, 5, 15));
        c1.setResponsiblePerson(dummyUser);
        complianceRepository.save(c1);
        
        StatutoryCompliance c2 = new StatutoryCompliance();
        c2.setComplianceType("ESI (Employees State Insurance)");
        c2.setApplicableTo("All Employees");
        c2.setFrequency("Monthly");
        c2.setNextDueDate(LocalDate.of(2026, 6, 15));
        c2.setStatus("Compliant");
        c2.setLastCompletedOn(LocalDate.of(2026, 5, 15));
        c2.setResponsiblePerson(dummyUser);
        complianceRepository.save(c2);
        
        StatutoryCompliance c3 = new StatutoryCompliance();
        c3.setComplianceType("Professional Tax");
        c3.setApplicableTo("All Employees");
        c3.setFrequency("Quarterly");
        c3.setNextDueDate(LocalDate.of(2026, 6, 30));
        c3.setStatus("Due Soon");
        c3.setLastCompletedOn(LocalDate.of(2026, 3, 31));
        c3.setResponsiblePerson(dummyUser);
        complianceRepository.save(c3);
        
        StatutoryCompliance c4 = new StatutoryCompliance();
        c4.setComplianceType("Bonus Act");
        c4.setApplicableTo("All Employees");
        c4.setFrequency("Annually");
        c4.setNextDueDate(LocalDate.of(2026, 8, 15));
        c4.setStatus("Overdue");
        c4.setResponsiblePerson(dummyUser);
        complianceRepository.save(c4);
        
        UpcomingAudit a1 = new UpcomingAudit();
        a1.setAuditName("Internal HR Audit");
        a1.setAuditDate(LocalDate.of(2026, 6, 15));
        a1.setStatus("Scheduled");
        auditRepository.save(a1);
        
        UpcomingAudit a2 = new UpcomingAudit();
        a2.setAuditName("ISO 27001 Audit");
        a2.setAuditDate(LocalDate.of(2026, 7, 10));
        a2.setStatus("Upcoming");
        auditRepository.save(a2);
    }
}
