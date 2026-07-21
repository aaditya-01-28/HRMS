package com.example.admindashboard.controller;

import com.example.admindashboard.model.LearningVendor;
import com.example.admindashboard.model.LearningBudget;
import com.example.admindashboard.model.LearningResource;
import com.example.admindashboard.repository.LearningVendorRepository;
import com.example.admindashboard.repository.LearningBudgetRepository;
import com.example.admindashboard.repository.LearningResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lnd")
public class LearningApiController {

    @Autowired
    private LearningVendorRepository vendorRepository;

    @Autowired
    private LearningBudgetRepository budgetRepository;

    @Autowired
    private LearningResourceRepository resourceRepository;

    // --- Vendors ---
    @GetMapping("/vendors")
    public List<LearningVendor> getAllVendors() {
        return vendorRepository.findAll();
    }

    @PostMapping("/vendors")
    public LearningVendor createVendor(@RequestBody LearningVendor vendor) {
        if(vendor.getPerformanceScore() == null) vendor.setPerformanceScore(0.0);
        if(vendor.getStatus() == null) vendor.setStatus("Active");
        return vendorRepository.save(vendor);
    }

    @DeleteMapping("/vendors/{id}")
    public ResponseEntity<?> deleteVendor(@PathVariable Long id) {
        vendorRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // --- Budgets ---
    @GetMapping("/budgets")
    public List<LearningBudget> getAllBudgets() {
        return budgetRepository.findAll();
    }

    @PostMapping("/budgets")
    public LearningBudget createBudget(@RequestBody LearningBudget budget) {
        if(budget.getSpentAmount() == null) budget.setSpentAmount(0.0);
        return budgetRepository.save(budget);
    }

    @DeleteMapping("/budgets/{id}")
    public ResponseEntity<?> deleteBudget(@PathVariable Long id) {
        budgetRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Autowired
    private com.example.admindashboard.repository.LearningStrategyRepository strategyRepository;

    @Autowired
    private com.example.admindashboard.repository.LearningProgramRepository programRepository;

    @Autowired
    private com.example.admindashboard.repository.AssessmentRepository assessmentRepository;

    @Autowired
    private com.example.admindashboard.repository.CertificationRepository certificationRepository;

    // --- Resources ---
    @GetMapping("/resources")
    public List<LearningResource> getAllResources() {
        return resourceRepository.findAll();
    }

    @PostMapping("/resources")
    public LearningResource createResource(@RequestBody LearningResource resource) {
        return resourceRepository.save(resource);
    }

    @DeleteMapping("/resources/{id}")
    public ResponseEntity<?> deleteResource(@PathVariable Long id) {
        resourceRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // --- Strategies ---
    @GetMapping("/strategies")
    public List<com.example.admindashboard.model.LearningStrategy> getAllStrategies() {
        return strategyRepository.findAll();
    }

    @PostMapping("/strategies")
    public com.example.admindashboard.model.LearningStrategy createStrategy(@RequestBody com.example.admindashboard.model.LearningStrategy strategy) {
        if (strategy.getStatus() == null) strategy.setStatus("Active");
        return strategyRepository.save(strategy);
    }

    // --- Programs ---
    @GetMapping("/programs")
    public List<com.example.admindashboard.model.LearningProgram> getAllPrograms() {
        return programRepository.findAll();
    }

    @PostMapping("/programs")
    public com.example.admindashboard.model.LearningProgram createProgram(@RequestBody com.example.admindashboard.model.LearningProgram program) {
        if (program.getStatus() == null) program.setStatus("Active");
        return programRepository.save(program);
    }

    // --- Assessments ---
    @GetMapping("/assessments")
    public List<com.example.admindashboard.model.Assessment> getAllAssessments() {
        return assessmentRepository.findAll();
    }

    @PostMapping("/assessments")
    public com.example.admindashboard.model.Assessment createAssessment(@RequestBody com.example.admindashboard.model.Assessment assessment) {
        if (assessment.getCode() == null || assessment.getCode().isEmpty()) {
            long count = assessmentRepository.count() + 1;
            assessment.setCode("ASMT-" + String.format("%03d", count));
        }
        if (assessment.getStatus() == null) assessment.setStatus("Active");
        if (assessment.getAttemptsCount() == null) assessment.setAttemptsCount(0);
        if (assessment.getPassRate() == null) assessment.setPassRate(0.0);
        return assessmentRepository.save(assessment);
    }

    // --- Certifications ---
    @GetMapping("/certifications")
    public List<com.example.admindashboard.model.Certification> getAllCertifications() {
        return certificationRepository.findAll();
    }
}
