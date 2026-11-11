package com.example.admindashboard.controller;

import org.springframework.transaction.annotation.Transactional;

import com.example.admindashboard.model.*;
import com.example.admindashboard.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/superadmin")
@PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_SUPER ADMIN') or hasAuthority('SUPER_ADMIN')")
public class SuperAdminApiController {

    @Autowired private CompanyDetailsRepository companyDetailsRepo;
    @Autowired private BranchDetailsRepository branchDetailsRepo;
    @Autowired private EpfSettingsRepository epfSettingsRepo;
    @Autowired private EsiSettingsRepository esiSettingsRepo;
    @Autowired private EstablishmentSettingsRepository establishmentSettingsRepo;

    @Autowired private EmployeeConfigRepository employeeConfigRepo;
    @Autowired private ClassificationConfigRepository classificationConfigRepo;
    @Autowired private HrCategoryRepository hrCategoryRepo;
    @Autowired private DocumentTypeRepository documentTypeRepo;
    @Autowired private AddressSetupRepository addressSetupRepo;
    @Autowired private StatutoryConfigRepository statutoryConfigRepo;

    @Autowired private UserRepository userRepo;
    @Autowired private EmployeeProfileRepository employeeProfileRepo;
    @Autowired private RoleRepository roleRepo;
    @Autowired private CandidateRepository candidateRepo;
    @Autowired private DepartmentEntityRepository departmentEntityRepo;
    @Autowired private TeamRepository teamRepo;
    @Autowired private EmployeeGroupRepository employeeGroupRepo;
    @Autowired private ItAssetRepository itAssetRepo;
    @Autowired private CourseEnrollmentRepository courseEnrollmentRepo;

    // ==========================================
    // 1. COMPANY & STATUTORY CONFIGS
    // ==========================================

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

    @GetMapping("/address-setup")
    public ResponseEntity<List<AddressSetup>> getAddressSetups() {
        return ResponseEntity.ok(addressSetupRepo.findAll());
    }

    @PostMapping("/address-setup")
    public ResponseEntity<AddressSetup> saveAddressSetup(@RequestBody AddressSetup setup) {
        return ResponseEntity.ok(addressSetupRepo.save(setup));
    }

    @DeleteMapping("/address-setup/{id}")
    public ResponseEntity<Void> deleteAddressSetup(@PathVariable Long id) {
        addressSetupRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/statutory-config")
    public ResponseEntity<StatutoryConfig> getStatutoryConfig() {
        return ResponseEntity.ok(statutoryConfigRepo.findAll().stream().findFirst().orElse(new StatutoryConfig()));
    }

    @PostMapping("/statutory-config")
    public ResponseEntity<StatutoryConfig> saveStatutoryConfig(@RequestBody StatutoryConfig config) {
        StatutoryConfig existing = statutoryConfigRepo.findAll().stream().findFirst().orElse(null);
        if (existing != null) {
            config.setId(existing.getId());
        }
        return ResponseEntity.ok(statutoryConfigRepo.save(config));
    }

    // ==========================================
    // 2. EMPLOYEES CRUD & DIRECTORY
    // ==========================================

    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeProfile>> getAllEmployees() {
        return ResponseEntity.ok(employeeProfileRepo.findAll());
    }

    @GetMapping("/employees/list")
    public ResponseEntity<List<Map<String, Object>>> getEmployeeFormattedList() {
        List<EmployeeProfile> profiles = employeeProfileRepo.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for (EmployeeProfile p : profiles) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", p.getId());
            map.put("employeeCode", p.getEmployeeCode() != null ? p.getEmployeeCode() : "EMP" + (100 + p.getId()));
            String name = "";
            if (p.getFirstName() != null) name += p.getFirstName();
            if (p.getLastName() != null) name += (name.isEmpty() ? "" : " ") + p.getLastName();
            if (name.isEmpty() && p.getUser() != null) name = p.getUser().getFullName();
            map.put("fullName", name.isEmpty() ? "Employee " + p.getId() : name);
            map.put("email", p.getUser() != null && p.getUser().getEmail() != null ? p.getUser().getEmail() : "");
            map.put("department", p.getDepartment() != null ? p.getDepartment() : "Engineering");
            map.put("designation", p.getDesignation() != null ? p.getDesignation() : "Associate");
            map.put("joiningDate", p.getJoiningDate() != null ? p.getJoiningDate().toString() : "01-09-2026");
            map.put("status", p.getUser() != null && p.getUser().getStatus() != null ? p.getUser().getStatus() : "ACTIVE");
            map.put("mobile", p.getMobileNumber() != null ? p.getMobileNumber() : "");
            map.put("role", p.getUser() != null && p.getUser().getRole() != null ? p.getUser().getRole().getRoleName() : "ROLE_EMPLOYEE");
            map.put("avatar", generateInitials(name));
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/employees/{id}")
    public ResponseEntity<EmployeeProfile> getEmployee(@PathVariable Long id) {
        return ResponseEntity.ok(employeeProfileRepo.findById(id).orElse(null));
    }

    @PostMapping("/employees/add")
    public ResponseEntity<?> addEmployee(@RequestBody Map<String, String> payload) {
        try {
            String email = payload.get("email");
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
            }
            email = email.trim().toLowerCase();

            String firstName = payload.getOrDefault("firstName", "New");
            String lastName = payload.getOrDefault("lastName", "Employee");
            String department = payload.getOrDefault("department", "Engineering");
            String designation = payload.getOrDefault("designation", "Software Engineer");
            String joiningDateStr = payload.getOrDefault("joiningDate", "");
            String mobile = payload.getOrDefault("mobile", "");
            String roleName = payload.getOrDefault("role", "ROLE_EMPLOYEE");

            User user = userRepo.findByUsername(email).orElse(null);
            if (user == null) {
                user = new User();
                user.setUsername(email);
                user.setEmail(email);
                user.setFullName(firstName + " " + lastName);
                user.setPassword("{noop}Welcome@123");
                user.setStatus("ACTIVE");

                Role role = roleRepo.findByRoleName(roleName)
                        .orElseGet(() -> roleRepo.findByRoleName("ROLE_EMPLOYEE").orElse(null));
                user.setRole(role);
                user = userRepo.save(user);
            }

            EmployeeProfile profile = employeeProfileRepo.findByUser_Username(email).orElse(new EmployeeProfile());
            profile.setUser(user);
            profile.setFirstName(firstName);
            profile.setLastName(lastName);
            profile.setDepartment(department);
            profile.setDesignation(designation);
            profile.setMobileNumber(mobile);
            profile.setJoiningDate(parseFlexibleDate(joiningDateStr));

            if (profile.getEmployeeCode() == null || profile.getEmployeeCode().isEmpty()) {
                profile.setEmployeeCode("EMP" + (1000 + employeeProfileRepo.count() + 1));
            }

            EmployeeProfile saved = employeeProfileRepo.save(profile);

            // Update department employee count
            departmentEntityRepo.findByDepartmentName(department).ifPresent(d -> {
                int cur = d.getEmployeesCount() != null ? d.getEmployeesCount() : 0;
                d.setEmployeesCount(cur + 1);
                departmentEntityRepo.save(d);
            });

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Employee created successfully!",
                "id", saved.getId(),
                "employeeCode", saved.getEmployeeCode(),
                "fullName", user.getFullName(),
                "email", user.getEmail(),
                "department", saved.getDepartment(),
                "designation", saved.getDesignation(),
                "status", user.getStatus()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/employees/{id}")
    @Transactional
    public ResponseEntity<?> updateEmployee(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        EmployeeProfile profile = employeeProfileRepo.findById(id).orElse(null);
        if (profile == null) {
            profile = employeeProfileRepo.findByUser_Id(id).orElse(null);
        }
        if (profile == null) {
            return ResponseEntity.notFound().build();
        }

        String oldDept = profile.getDepartment();

        if (payload.containsKey("firstName")) profile.setFirstName(payload.get("firstName"));
        if (payload.containsKey("lastName")) profile.setLastName(payload.get("lastName"));
        if (payload.containsKey("designation")) profile.setDesignation(payload.get("designation"));
        if (payload.containsKey("department")) profile.setDepartment(payload.get("department"));
        if (payload.containsKey("mobile")) profile.setMobileNumber(payload.get("mobile"));
        else if (payload.containsKey("mobileNumber")) profile.setMobileNumber(payload.get("mobileNumber"));

        User user = profile.getUser();
        if (user != null) {
            String fName = profile.getFirstName() != null ? profile.getFirstName() : "";
            String lName = profile.getLastName() != null ? profile.getLastName() : "";
            user.setFullName((fName + " " + lName).trim());
            if (payload.containsKey("status")) {
                user.setStatus(payload.get("status").toUpperCase());
            }
            if (payload.containsKey("email")) {
                user.setEmail(payload.get("email").trim().toLowerCase());
            }
            userRepo.save(user);
        }

        EmployeeProfile saved = employeeProfileRepo.save(profile);

        // Sync department employee counts if department changed
        String newDept = saved.getDepartment();
        if (oldDept != null && newDept != null && !oldDept.equalsIgnoreCase(newDept)) {
            departmentEntityRepo.findByDepartmentNameIgnoreCase(oldDept).ifPresent(d -> {
                long c = employeeProfileRepo.countByDepartmentIgnoreCase(oldDept);
                d.setEmployeesCount((int) c);
                departmentEntityRepo.save(d);
            });
            departmentEntityRepo.findByDepartmentNameIgnoreCase(newDept).ifPresent(d -> {
                long c = employeeProfileRepo.countByDepartmentIgnoreCase(newDept);
                d.setEmployeesCount((int) c);
                departmentEntityRepo.save(d);
            });
        }

        String fullName = (saved.getFirstName() != null ? saved.getFirstName() : "") + " " + (saved.getLastName() != null ? saved.getLastName() : "");
        return ResponseEntity.ok(Map.of(
            "success", true,
            "id", saved.getId(),
            "employeeCode", saved.getEmployeeCode() != null ? saved.getEmployeeCode() : "EMP" + saved.getId(),
            "fullName", fullName.trim(),
            "department", saved.getDepartment() != null ? saved.getDepartment() : "Engineering",
            "designation", saved.getDesignation() != null ? saved.getDesignation() : "Associate",
            "status", user != null && user.getStatus() != null ? user.getStatus() : "ACTIVE",
            "mobile", saved.getMobileNumber() != null ? saved.getMobileNumber() : "",
            "email", user != null && user.getEmail() != null ? user.getEmail() : ""
        ));
    }

    @PutMapping("/employees/{id}/status")
    @Transactional
    public ResponseEntity<?> updateEmployeeStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String status = payload.getOrDefault("status", "ACTIVE").toUpperCase();
        EmployeeProfile profile = employeeProfileRepo.findById(id).orElse(null);
        User u = null;
        if (profile != null) {
            u = profile.getUser();
        } else {
            profile = employeeProfileRepo.findByUser_Id(id).orElse(null);
            if (profile != null) {
                u = profile.getUser();
            } else {
                u = userRepo.findById(id).orElse(null);
            }
        }

        if (profile == null && u == null) {
            return ResponseEntity.notFound().build();
        }

        if (u != null) {
            u.setStatus(status);
            userRepo.save(u);
        }
        if (profile != null && profile.getUser() != null) {
            profile.getUser().setStatus(status);
            employeeProfileRepo.save(profile);
        }

        return ResponseEntity.ok(Map.of("success", true, "status", status));
    }

    @DeleteMapping("/employees/{id}")
    @Transactional
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        EmployeeProfile profile = employeeProfileRepo.findById(id).orElse(null);
        User u = null;
        if (profile != null) {
            u = profile.getUser();
        } else {
            profile = employeeProfileRepo.findByUser_Id(id).orElse(null);
            if (profile != null) {
                u = profile.getUser();
            } else {
                u = userRepo.findById(id).orElse(null);
            }
        }

        if (profile == null && u == null) {
            return ResponseEntity.notFound().build();
        }

        // 1. Decrement department headcount if present
        if (profile != null && profile.getDepartment() != null) {
            try {
                DepartmentEntity dept = departmentEntityRepo.findByDepartmentNameIgnoreCase(profile.getDepartment()).orElse(null);
                if (dept != null && dept.getEmployeesCount() != null && dept.getEmployeesCount() > 0) {
                    dept.setEmployeesCount(dept.getEmployeesCount() - 1);
                    departmentEntityRepo.save(dept);
                }
            } catch (Exception ignored) {}
        }

        // 2. Remove any CourseEnrollments referencing this profile
        if (profile != null) {
            try {
                courseEnrollmentRepo.deleteByEmployeeId(profile.getId());
            } catch (Exception ignored) {}
        }

        // 3. Sever relationship with User and deactivate User account
        if (u != null) {
            u.setEmployeeProfile(null);
            u.setStatus("INACTIVE");
            userRepo.save(u);
        }

        // 4. Safely delete the EmployeeProfile from the database
        if (profile != null) {
            Long profileId = profile.getId();
            profile.setUser(null);
            try {
                employeeProfileRepo.delete(profile);
                employeeProfileRepo.flush();
            } catch (Exception e) {
                try {
                    employeeProfileRepo.deleteProfileByIdNative(profileId);
                } catch (Exception ex) {
                    System.err.println("Profile deletion note: " + ex.getMessage());
                }
            }
        }

        return ResponseEntity.ok(Map.of("success", true, "message", "Employee removed successfully"));
    }

    @PostMapping("/employees/bulk-update")
    public ResponseEntity<?> bulkUpdateEmployees(@RequestBody Map<String, Object> req) {
        try {
            @SuppressWarnings("unchecked")
            List<?> idsRaw = (List<?>) req.get("employeeIds");
            String field = (String) req.get("field");
            String value = (String) req.get("value");

            if (idsRaw == null || idsRaw.isEmpty() || field == null || value == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Missing required fields"));
            }

            int count = 0;
            for (Object obj : idsRaw) {
                Long empId = Long.valueOf(obj.toString());
                Optional<EmployeeProfile> pOpt = employeeProfileRepo.findById(empId);
                if (pOpt.isPresent()) {
                    EmployeeProfile p = pOpt.get();
                    switch (field.toLowerCase()) {
                        case "department":
                            p.setDepartment(value);
                            break;
                        case "designation":
                            p.setDesignation(value);
                            break;
                        case "status":
                            if (p.getUser() != null) {
                                p.getUser().setStatus(value.toUpperCase());
                                userRepo.save(p.getUser());
                            }
                            break;
                        case "manager":
                        case "authority":
                            p.setReportingManager(value);
                            break;
                        case "role":
                            if (p.getUser() != null) {
                                roleRepo.findByRoleName(value).ifPresent(r -> {
                                    p.getUser().setRole(r);
                                    userRepo.save(p.getUser());
                                });
                            }
                            break;
                    }
                    employeeProfileRepo.save(p);
                    count++;
                }
            }
            return ResponseEntity.ok(Map.of("success", true, "message", "Bulk update completed for " + count + " employees", "count", count));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/employees/import")
    public ResponseEntity<?> importEmployees(@RequestBody List<Map<String, String>> employeesData) {
        try {
            int imported = 0;
            for (Map<String, String> row : employeesData) {
                String email = row.get("email");
                if (email == null || email.trim().isEmpty()) continue;
                email = email.trim().toLowerCase();

                String firstName = row.getOrDefault("firstName", "Employee");
                String lastName = row.getOrDefault("lastName", "");
                String department = row.getOrDefault("department", "Engineering");
                String designation = row.getOrDefault("designation", "Software Engineer");
                String joiningDate = row.getOrDefault("joiningDate", "");
                String mobile = row.getOrDefault("mobile", "");

                User user = userRepo.findByUsername(email).orElse(null);
                if (user == null) {
                    user = new User();
                    user.setUsername(email);
                    user.setEmail(email);
                    user.setFullName(firstName + " " + lastName);
                    user.setPassword("{noop}Welcome@123");
                    user.setStatus("ACTIVE");
                    user.setRole(roleRepo.findByRoleName("ROLE_EMPLOYEE").orElse(null));
                    user = userRepo.save(user);
                }

                EmployeeProfile p = employeeProfileRepo.findByUser_Username(email).orElse(new EmployeeProfile());
                p.setUser(user);
                p.setFirstName(firstName);
                p.setLastName(lastName);
                p.setDepartment(department);
                p.setDesignation(designation);
                p.setMobileNumber(mobile);
                p.setJoiningDate(parseFlexibleDate(joiningDate));
                if (p.getEmployeeCode() == null || p.getEmployeeCode().isEmpty()) {
                    p.setEmployeeCode("EMP" + (1000 + employeeProfileRepo.count() + 1));
                }
                employeeProfileRepo.save(p);
                imported++;
            }
            return ResponseEntity.ok(Map.of("success", true, "imported", imported));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // ==========================================
    // 3. CANDIDATES & ONBOARDING WORKFLOW
    // ==========================================

    @GetMapping("/candidates")
    public ResponseEntity<List<Candidate>> getAllCandidates() {
        return ResponseEntity.ok(candidateRepo.findAll());
    }

    @PostMapping("/candidates")
    public ResponseEntity<Candidate> createCandidate(@RequestBody Candidate candidate) {
        if (candidate.getCandidateCode() == null || candidate.getCandidateCode().isEmpty()) {
            candidate.setCandidateCode("CND" + (1000 + candidateRepo.count() + 1));
        }
        if (candidate.getOnboardStatus() == null || candidate.getOnboardStatus().isEmpty()) {
            candidate.setOnboardStatus("In Progress");
        }
        return ResponseEntity.ok(candidateRepo.save(candidate));
    }

    @PutMapping("/candidates/{id}")
    public ResponseEntity<Candidate> updateCandidate(@PathVariable Long id, @RequestBody Candidate updated) {
        Candidate existing = candidateRepo.findById(id).orElse(null);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        updated.setId(id);
        return ResponseEntity.ok(candidateRepo.save(updated));
    }

    @DeleteMapping("/candidates/{id}")
    public ResponseEntity<?> deleteCandidate(@PathVariable Long id) {
        candidateRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/candidates/{id}/convert")
    public ResponseEntity<?> convertCandidateToEmployee(@PathVariable Long id) {
        Optional<Candidate> candOpt = candidateRepo.findById(id);
        if (candOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Candidate c = candOpt.get();

        String email = c.getEmail();
        if (email == null || email.trim().isEmpty()) {
            String sanitized = c.getName().toLowerCase().replaceAll("[^a-z0-9]", ".");
            email = sanitized + "@textora.com";
        }
        email = email.trim().toLowerCase();

        User user = userRepo.findByUsername(email).orElse(null);
        if (user == null) {
            user = new User();
            user.setUsername(email);
            user.setEmail(email);
            user.setFullName(c.getName());
            user.setPassword("{noop}Welcome@123");
            user.setStatus("ACTIVE");
            user.setRole(roleRepo.findByRoleName("ROLE_EMPLOYEE").orElse(null));
            user = userRepo.save(user);
        }

        EmployeeProfile profile = employeeProfileRepo.findByUser_Username(email).orElse(new EmployeeProfile());
        profile.setUser(user);

        String[] parts = c.getName() != null ? c.getName().split("\\s+", 2) : new String[]{"Candidate", ""};
        profile.setFirstName(parts[0]);
        profile.setLastName(parts.length > 1 ? parts[1] : "");
        profile.setDepartment(c.getDepartment() != null && !c.getDepartment().isEmpty() ? c.getDepartment() : "Engineering");
        profile.setDesignation(c.getJobTitle() != null && !c.getJobTitle().isEmpty() ? c.getJobTitle() : "Software Engineer");
        profile.setMobileNumber(c.getContactNumber());
        profile.setJoiningDate(parseFlexibleDate(c.getJoiningDate()));
        profile.setAadharNo(c.getAadharNumber());
        profile.setPanNo(c.getPanNumber());
        profile.setReportingManager(c.getReportingAuthority());

        if (profile.getEmployeeCode() == null || profile.getEmployeeCode().isEmpty()) {
            profile.setEmployeeCode("EMP" + (1000 + employeeProfileRepo.count() + 1));
        }

        employeeProfileRepo.save(profile);

        c.setOnboardStatus("Completed");
        candidateRepo.save(c);

        // Update department count
        departmentEntityRepo.findByDepartmentName(profile.getDepartment()).ifPresent(d -> {
            int cur = d.getEmployeesCount() != null ? d.getEmployeesCount() : 0;
            d.setEmployeesCount(cur + 1);
            departmentEntityRepo.save(d);
        });

        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Candidate successfully converted to Active Employee!",
            "employeeCode", profile.getEmployeeCode(),
            "fullName", user.getFullName(),
            "department", profile.getDepartment(),
            "designation", profile.getDesignation()
        ));
    }

    // ==========================================
    // 4. APPROVAL WORKFLOW
    // ==========================================

    @GetMapping("/approvals/pending")
    public ResponseEntity<List<Map<String, Object>>> getPendingApprovals() {
        List<Candidate> pendingCandidates = candidateRepo.findByOnboardStatus("In Progress");
        List<Map<String, Object>> result = new ArrayList<>();
        for (Candidate c : pendingCandidates) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("name", c.getName());
            map.put("code", c.getCandidateCode() != null ? c.getCandidateCode() : "CND" + c.getId());
            map.put("role", c.getJobTitle() != null ? c.getJobTitle() : "Associate");
            map.put("department", c.getDepartment() != null ? c.getDepartment() : "Engineering");
            map.put("email", c.getEmail() != null ? c.getEmail() : "");
            map.put("mobile", c.getContactNumber() != null ? c.getContactNumber() : "");
            map.put("joiningDate", c.getJoiningDate() != null ? c.getJoiningDate() : "01-04-2026");
            map.put("status", "Pending");
            map.put("type", "Candidate");
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/approvals/{id}/approve")
    @Transactional
    public ResponseEntity<?> approvePendingItem(@PathVariable Long id) {
        EmployeeProfile profile = employeeProfileRepo.findById(id).orElse(null);
        if (profile == null) {
            profile = employeeProfileRepo.findByUser_Id(id).orElse(null);
        }
        if (profile != null) {
            User u = profile.getUser();
            if (u != null) {
                u.setStatus("ACTIVE");
                userRepo.save(u);
            }
            return ResponseEntity.ok(Map.of("success", true, "message", "Employee " + profile.getEmployeeCode() + " approved & activated!"));
        }

        return convertCandidateToEmployee(id);
    }

    @PostMapping("/approvals/{id}/reject")
    @Transactional
    public ResponseEntity<?> rejectPendingItem(@PathVariable Long id) {
        EmployeeProfile profile = employeeProfileRepo.findById(id).orElse(null);
        if (profile == null) {
            profile = employeeProfileRepo.findByUser_Id(id).orElse(null);
        }
        if (profile != null) {
            User u = profile.getUser();
            if (u != null) {
                u.setStatus("REJECTED");
                userRepo.save(u);
            }
            return ResponseEntity.ok(Map.of("success", true, "message", "Approval rejected for employee " + profile.getEmployeeCode()));
        }

        candidateRepo.findById(id).ifPresent(c -> {
            c.setOnboardStatus("Rejected");
            candidateRepo.save(c);
        });
        return ResponseEntity.ok(Map.of("success", true, "message", "Approval rejected"));
    }

    // ==========================================
    // 5. EMPLOYEE GROUPS CRUD
    // ==========================================

    @GetMapping("/groups")
    public ResponseEntity<List<EmployeeGroup>> getAllGroups() {
        return ResponseEntity.ok(employeeGroupRepo.findAll());
    }

    @PostMapping("/groups")
    public ResponseEntity<EmployeeGroup> createGroup(@RequestBody EmployeeGroup group) {
        if (group.getStatus() == null || group.getStatus().isEmpty()) {
            group.setStatus("Active");
        }
        if (group.getTotalEmployees() == null) {
            group.setTotalEmployees(0);
        }
        return ResponseEntity.ok(employeeGroupRepo.save(group));
    }

    @PutMapping("/groups/{id}")
    public ResponseEntity<EmployeeGroup> updateGroup(@PathVariable Long id, @RequestBody EmployeeGroup updated) {
        EmployeeGroup existing = employeeGroupRepo.findById(id).orElse(null);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        updated.setId(id);
        return ResponseEntity.ok(employeeGroupRepo.save(updated));
    }

    @DeleteMapping("/groups/{id}")
    public ResponseEntity<?> deleteGroup(@PathVariable Long id) {
        employeeGroupRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // ==========================================
    // 6. DEPARTMENTS & TEAMS (ORGANIZATION CHART)
    // ==========================================

    @GetMapping("/departments")
    public ResponseEntity<List<DepartmentEntity>> getAllDepartments() {
        return ResponseEntity.ok(departmentEntityRepo.findAll());
    }

    @PostMapping("/departments")
    public ResponseEntity<DepartmentEntity> createDepartment(@RequestBody Map<String, String> payload) {
        DepartmentEntity d = new DepartmentEntity();
        String deptName = payload.containsKey("departmentName") ? payload.get("departmentName") : payload.get("name");
        if (deptName == null || deptName.trim().isEmpty()) {
            deptName = "New Department";
        }
        d.setDepartmentName(deptName.trim());

        String loc = payload.containsKey("locations") ? payload.get("locations") : payload.get("location");
        d.setLocations(loc != null && !loc.trim().isEmpty() ? loc.trim() : "Raipur, India");

        d.setStatus(payload.getOrDefault("status", "Active"));

        String head = payload.containsKey("headName") ? payload.get("headName") : payload.get("head");
        d.setHeadName(head != null && !head.trim().isEmpty() ? head.trim() : "Department Head");

        d.setTotalTeams(payload.containsKey("totalTeams") ? Integer.parseInt(payload.get("totalTeams")) : 1);
        d.setEmployeesCount(0);
        return ResponseEntity.ok(departmentEntityRepo.save(d));
    }

    @PutMapping("/departments/{id}")
    @Transactional
    public ResponseEntity<DepartmentEntity> updateDepartment(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        DepartmentEntity d = departmentEntityRepo.findById(id).orElse(null);
        if (d == null) {
            return ResponseEntity.notFound().build();
        }
        String oldName = d.getDepartmentName();
        String newName = payload.containsKey("departmentName") ? payload.get("departmentName") : payload.get("name");
        if (newName != null && !newName.trim().isEmpty()) {
            newName = newName.trim();
            d.setDepartmentName(newName);
            // Cascade rename to employees and teams
            if (oldName != null && !oldName.equalsIgnoreCase(newName)) {
                List<EmployeeProfile> emps = employeeProfileRepo.findByDepartmentIgnoreCase(oldName);
                for (EmployeeProfile emp : emps) {
                    emp.setDepartment(newName);
                    employeeProfileRepo.save(emp);
                }
                List<Team> teams = teamRepo.findByDepartmentNameIgnoreCase(oldName);
                for (Team t : teams) {
                    t.setDepartmentName(newName);
                    teamRepo.save(t);
                }
            }
        }
        if (payload.containsKey("locations")) d.setLocations(payload.get("locations"));
        else if (payload.containsKey("location")) d.setLocations(payload.get("location"));

        if (payload.containsKey("status")) d.setStatus(payload.get("status"));

        if (payload.containsKey("headName")) d.setHeadName(payload.get("headName"));
        else if (payload.containsKey("head")) d.setHeadName(payload.get("head"));

        if (payload.containsKey("totalTeams")) {
            try { d.setTotalTeams(Integer.parseInt(payload.get("totalTeams"))); } catch(Exception ignored) {}
        }
        return ResponseEntity.ok(departmentEntityRepo.save(d));
    }

    @DeleteMapping("/departments/{id}")
    public ResponseEntity<?> deleteDepartment(@PathVariable Long id) {
        departmentEntityRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/departments/{name}/employees")
    public ResponseEntity<List<Map<String, Object>>> getDepartmentEmployees(@PathVariable String name) {
        List<EmployeeProfile> list = employeeProfileRepo.findByDepartmentIgnoreCase(name);
        List<Map<String, Object>> res = new ArrayList<>();
        for (EmployeeProfile p : list) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", p.getId());
            m.put("code", p.getEmployeeCode());
            String fullName = (p.getFirstName() != null ? p.getFirstName() : "") + " " + (p.getLastName() != null ? p.getLastName() : "");
            m.put("name", fullName.trim().isEmpty() && p.getUser() != null ? p.getUser().getFullName() : fullName.trim());
            m.put("designation", p.getDesignation() != null ? p.getDesignation() : "Associate");
            m.put("status", p.getUser() != null && p.getUser().getStatus() != null ? p.getUser().getStatus() : "ACTIVE");
            m.put("avatar", generateInitials(m.get("name").toString()));
            res.add(m);
        }
        return ResponseEntity.ok(res);
    }

    @GetMapping("/departments/{name}/teams")
    public ResponseEntity<List<Team>> getDepartmentTeams(@PathVariable String name) {
        return ResponseEntity.ok(teamRepo.findByDepartmentNameIgnoreCase(name));
    }

    @GetMapping("/teams")
    public ResponseEntity<List<Team>> getAllTeams() {
        return ResponseEntity.ok(teamRepo.findAll());
    }

    @PostMapping("/teams")
    public ResponseEntity<Team> createTeam(@RequestBody Map<String, String> payload) {
        Team t = new Team();
        t.setTeamName(payload.getOrDefault("teamName", "New Team"));
        t.setDescription(payload.getOrDefault("description", ""));
        String dept = payload.getOrDefault("departmentName", "Engineering");
        t.setDepartmentName(dept);
        Team saved = teamRepo.save(t);

        // Update totalTeams in DepartmentEntity
        departmentEntityRepo.findByDepartmentNameIgnoreCase(dept).ifPresent(d -> {
            long c = teamRepo.countByDepartmentNameIgnoreCase(dept);
            d.setTotalTeams((int) c);
            departmentEntityRepo.save(d);
        });

        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/teams/{id}")
    public ResponseEntity<?> deleteTeam(@PathVariable Long id) {
        teamRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // ==========================================
    // 7. IT ASSETS MANAGEMENT
    // ==========================================

    @GetMapping("/assets")
    public ResponseEntity<List<ItAsset>> getAllAssets() {
        return ResponseEntity.ok(itAssetRepo.findAll());
    }

    @PostMapping("/assets")
    public ResponseEntity<ItAsset> createAsset(@RequestBody ItAsset asset) {
        if (asset.getStatus() == null || asset.getStatus().isEmpty()) {
            asset.setStatus("Unassigned");
        }
        return ResponseEntity.ok(itAssetRepo.save(asset));
    }

    @PutMapping("/assets/{id}")
    public ResponseEntity<ItAsset> updateAsset(@PathVariable Long id, @RequestBody ItAsset updated) {
        ItAsset existing = itAssetRepo.findById(id).orElse(null);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        updated.setId(id);
        return ResponseEntity.ok(itAssetRepo.save(updated));
    }

    @DeleteMapping("/assets/{id}")
    public ResponseEntity<?> deleteAsset(@PathVariable Long id) {
        itAssetRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/assets/{id}/allocate")
    public ResponseEntity<?> allocateAsset(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        ItAsset asset = itAssetRepo.findById(id).orElse(null);
        if (asset == null) {
            return ResponseEntity.notFound().build();
        }
        String assignedTo = payload.get("assignedTo");
        asset.setAssignedTo(assignedTo);
        asset.setStatus("Assigned");
        itAssetRepo.save(asset);
        return ResponseEntity.ok(Map.of("success", true, "message", "Asset allocated to " + assignedTo));
    }

    @PostMapping("/assets/{id}/return")
    public ResponseEntity<?> returnAsset(@PathVariable Long id) {
        ItAsset asset = itAssetRepo.findById(id).orElse(null);
        if (asset == null) {
            return ResponseEntity.notFound().build();
        }
        asset.setAssignedTo(null);
        asset.setStatus("Unassigned");
        itAssetRepo.save(asset);
        return ResponseEntity.ok(Map.of("success", true, "message", "Asset returned to inventory"));
    }

    // ==========================================
    // HELPER METHODS
    // ==========================================

    private LocalDate parseFlexibleDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return LocalDate.now();
        }
        String s = dateStr.trim();
        String[] formats = {
            "dd-MM-yyyy", "yyyy-MM-dd", "dd/MM/yyyy",
            "d/MMM/yyyy", "dd/MMM/yyyy", "d-MMM-yyyy", "dd-MMM-yyyy",
            "MM/dd/yyyy", "yyyy/MM/dd"
        };
        for (String fmt : formats) {
            try {
                return LocalDate.parse(s, DateTimeFormatter.ofPattern(fmt, Locale.ENGLISH));
            } catch (Exception ignored) {}
        }
        return LocalDate.now();
    }

    private String generateInitials(String name) {
        if (name == null || name.trim().isEmpty()) return "EM";
        String[] parts = name.trim().split("\\s+");
        if (parts.length >= 2) {
            return ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase();
        }
        return name.substring(0, Math.min(2, name.length())).toUpperCase();
    }
}
