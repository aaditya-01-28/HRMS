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
}
