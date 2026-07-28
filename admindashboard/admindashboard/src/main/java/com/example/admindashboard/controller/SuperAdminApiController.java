package com.example.admindashboard.controller;

import com.example.admindashboard.model.*;
import com.example.admindashboard.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/superadmin")
@PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_SUPER ADMIN') or hasAuthority('SUPER_ADMIN')")
public class SuperAdminApiController {

    @Autowired private CompanyDetailsRepository companyDetailsRepo;
    @Autowired private BranchDetailsRepository branchDetailsRepo;
    @Autowired private EpfSettingsRepository epfSettingsRepo;
    @Autowired private EsiSettingsRepository esiSettingsRepo;
    @Autowired private EstablishmentSettingsRepository establishmentSettingsRepo;

    // --- Company Details ---
    @GetMapping("/company")
    public ResponseEntity<CompanyDetails> getCompanyDetails() {
        return ResponseEntity.ok(companyDetailsRepo.findAll().stream().findFirst().orElse(new CompanyDetails()));
    }

    @PostMapping("/company")
    public ResponseEntity<CompanyDetails> saveCompanyDetails(@RequestBody CompanyDetails details) {
        CompanyDetails existing = companyDetailsRepo.findAll().stream().findFirst().orElse(null);
        if (existing != null) {
            details.setId(existing.getId());
        }
        return ResponseEntity.ok(companyDetailsRepo.save(details));
    }

    // --- Branch Details ---
    @GetMapping("/branch")
    public ResponseEntity<List<BranchDetails>> getAllBranches() {
        return ResponseEntity.ok(branchDetailsRepo.findAll());
    }

    @PostMapping("/branch")
    public ResponseEntity<BranchDetails> saveBranch(@RequestBody BranchDetails branch) {
        return ResponseEntity.ok(branchDetailsRepo.save(branch));
    }

    @DeleteMapping("/branch/{id}")
    public ResponseEntity<?> deleteBranch(@PathVariable Long id) {
        branchDetailsRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // --- EPF Settings ---
    @GetMapping("/epf")
    public ResponseEntity<EpfSettings> getEpfSettings() {
        return ResponseEntity.ok(epfSettingsRepo.findAll().stream().findFirst().orElse(new EpfSettings()));
    }

    @PostMapping("/epf")
    public ResponseEntity<EpfSettings> saveEpfSettings(@RequestBody EpfSettings settings) {
        EpfSettings existing = epfSettingsRepo.findAll().stream().findFirst().orElse(null);
        if (existing != null) {
            settings.setId(existing.getId());
        }
        return ResponseEntity.ok(epfSettingsRepo.save(settings));
    }

    // --- ESI Settings ---
    @GetMapping("/esi")
    public ResponseEntity<EsiSettings> getEsiSettings() {
        return ResponseEntity.ok(esiSettingsRepo.findAll().stream().findFirst().orElse(new EsiSettings()));
    }

    @PostMapping("/esi")
    public ResponseEntity<EsiSettings> saveEsiSettings(@RequestBody EsiSettings settings) {
        EsiSettings existing = esiSettingsRepo.findAll().stream().findFirst().orElse(null);
        if (existing != null) {
            settings.setId(existing.getId());
        }
        return ResponseEntity.ok(esiSettingsRepo.save(settings));
    }

    // --- Establishment Settings ---
    @GetMapping("/establishment")
    public ResponseEntity<EstablishmentSettings> getEstablishmentSettings() {
        return ResponseEntity.ok(establishmentSettingsRepo.findAll().stream().findFirst().orElse(new EstablishmentSettings()));
    }

    @PostMapping("/establishment")
    public ResponseEntity<EstablishmentSettings> saveEstablishmentSettings(@RequestBody EstablishmentSettings details) {
        EstablishmentSettings existing = establishmentSettingsRepo.findAll().stream().findFirst().orElse(null);
        if (existing != null) {
            details.setId(existing.getId());
        }
        return ResponseEntity.ok(establishmentSettingsRepo.save(details));
    }

    // --- Phase A: Employee Configuration ---

    @Autowired private EmployeeConfigRepository employeeConfigRepo;
    @Autowired private ClassificationConfigRepository classificationConfigRepo;
    @Autowired private HrCategoryRepository hrCategoryRepo;
    @Autowired private DocumentTypeRepository documentTypeRepo;

    // 1. Employee Config Details
    @GetMapping("/employee-config")
    public ResponseEntity<EmployeeConfig> getEmployeeConfig() {
        return ResponseEntity.ok(employeeConfigRepo.findAll().stream().findFirst().orElse(new EmployeeConfig()));
    }

    @PostMapping("/employee-config")
    public ResponseEntity<EmployeeConfig> saveEmployeeConfig(@RequestBody EmployeeConfig config) {
        EmployeeConfig existing = employeeConfigRepo.findAll().stream().findFirst().orElse(null);
        if (existing != null) {
            config.setId(existing.getId());
        }
        return ResponseEntity.ok(employeeConfigRepo.save(config));
    }

    // 2. Classifications
    @GetMapping("/classifications")
    public ResponseEntity<List<ClassificationConfig>> getClassifications() {
        return ResponseEntity.ok(classificationConfigRepo.findAll());
    }

    @PostMapping("/classifications")
    public ResponseEntity<ClassificationConfig> saveClassification(@RequestBody ClassificationConfig config) {
        return ResponseEntity.ok(classificationConfigRepo.save(config));
    }

    @DeleteMapping("/classifications/{id}")
    public ResponseEntity<Void> deleteClassification(@PathVariable Long id) {
        classificationConfigRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // 3. HR Categories
    @GetMapping("/hr-categories")
    public ResponseEntity<List<HrCategory>> getHrCategories() {
        return ResponseEntity.ok(hrCategoryRepo.findAll());
    }

    @PostMapping("/hr-categories")
    public ResponseEntity<HrCategory> saveHrCategory(@RequestBody HrCategory category) {
        return ResponseEntity.ok(hrCategoryRepo.save(category));
    }

    @DeleteMapping("/hr-categories/{id}")
    public ResponseEntity<Void> deleteHrCategory(@PathVariable Long id) {
        hrCategoryRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // 4. Document Types
    @GetMapping("/document-types")
    public ResponseEntity<List<DocumentType>> getDocumentTypes() {
        return ResponseEntity.ok(documentTypeRepo.findAll());
    }

    @PostMapping("/document-types")
    public ResponseEntity<DocumentType> saveDocumentType(@RequestBody DocumentType documentType) {
        return ResponseEntity.ok(documentTypeRepo.save(documentType));
    }

    @DeleteMapping("/document-types/{id}")
    public ResponseEntity<Void> deleteDocumentType(@PathVariable Long id) {
        documentTypeRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
