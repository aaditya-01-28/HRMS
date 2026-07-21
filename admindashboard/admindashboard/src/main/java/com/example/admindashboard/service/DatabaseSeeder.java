package com.example.admindashboard.service;

import com.example.admindashboard.model.*;
import com.example.admindashboard.repository.ClientRepository;
import com.example.admindashboard.repository.PermissionRepository;
import com.example.admindashboard.repository.RoleRepository;
import com.example.admindashboard.repository.UserRepository;
import com.example.admindashboard.repository.FacilityVendorRepository;
import com.example.admindashboard.repository.FacilityDeviceRepository;
import com.example.admindashboard.repository.FacilityServiceRepository;
import com.example.admindashboard.repository.FacilityContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private com.example.admindashboard.repository.LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private com.example.admindashboard.repository.HospitalRepository hospitalRepository;

    @Autowired
    private com.example.admindashboard.repository.InsurancePolicyRepository insurancePolicyRepository;

    // NEW: Injecting JdbcTemplate to fix the database constraint
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FacilityVendorRepository facilityVendorRepository;

    @Autowired
    private FacilityDeviceRepository facilityDeviceRepository;

    @Autowired
    private FacilityServiceRepository facilityServiceRepository;

    @Autowired
    private FacilityContractRepository facilityContractRepository;

    @Autowired
    private com.example.admindashboard.repository.ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private com.example.admindashboard.repository.RewardMerchandiseRepository rewardMerchandiseRepository;

    @Autowired
    private com.example.admindashboard.repository.RewardBudgetRepository rewardBudgetRepository;

    @Autowired
    private com.example.admindashboard.repository.RewardProgramRepository rewardProgramRepository;

    @Autowired
    private com.example.admindashboard.repository.RewardRuleRepository rewardRuleRepository;

    @Autowired
    private com.example.admindashboard.repository.TeamPointAllocationRepository teamPointAllocationRepository;

    @Autowired
    private com.example.admindashboard.repository.BudgetActivityRepository budgetActivityRepository;

    @Autowired
    private com.example.admindashboard.repository.ItAssetRepository itAssetRepository;

    @Autowired
    private com.example.admindashboard.repository.SecurityPolicyRepository securityPolicyRepository;

    @Autowired
    private com.example.admindashboard.repository.BackupScheduleRepository backupScheduleRepository;

    @Autowired
    private com.example.admindashboard.repository.ItBroadcastRepository itBroadcastRepository;

    @Autowired
    private com.example.admindashboard.repository.LearningStrategyRepository learningStrategyRepository;

    @Autowired
    private com.example.admindashboard.repository.LearningProgramRepository learningProgramRepository;

    @Autowired
    private com.example.admindashboard.repository.AssessmentRepository assessmentRepository;

    @Autowired
    private com.example.admindashboard.repository.CertificationRepository certificationRepository;

    @Override
    public void run(String... args) throws Exception {
                // Always update existing seeded users to ensure they have correct employeeCode and department
        updateSeededEmployee("EMP114", "Engineering", "Marketing Lead");
        updateSeededEmployee("EMP187", "Product", "Project Manager");
        updateSeededEmployee("EMP129", "Engineering", "IOS Developer");
        updateSeededEmployee("EMP201", "HR", "HR Director");
        updateSeededEmployee("ADMIN001", "IT", "Company Admin / IT Admin");
        updateSeededEmployee("EMP0001", "IT", "Senior IT Head");

        System.out.println("=========================================================");
        System.out.println("🔄 Checking database for roles, permissions, and default accounts...");

        // ==========================================
        // DATABASE MIGRATION FIX
        // ==========================================
        try {
            System.out.println("🛠️ Patching legacy database constraints...");
            // This drops the strict NOT NULL rule from the old unused role column
            jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN role DROP NOT NULL");
            System.out.println("✅ Legacy role column successfully patched.");
        } catch (Exception e) {
            // If the column doesn't exist or is already patched, we just safely ignore it
            System.out.println("⚡ Legacy role column is already patched or removed.");
        }

        try {
            System.out.println("🛠️ Adding missing recruitment columns to database...");
            jdbcTemplate.execute("ALTER TABLE referrals ADD COLUMN IF NOT EXISTS candidate_current_role VARCHAR(255)");
            jdbcTemplate.execute("ALTER TABLE referrals ADD COLUMN IF NOT EXISTS current_company VARCHAR(255)");
            jdbcTemplate.execute("ALTER TABLE referrals ADD COLUMN IF NOT EXISTS experience_years VARCHAR(255)");
            jdbcTemplate.execute("ALTER TABLE referrals ADD COLUMN IF NOT EXISTS next_step VARCHAR(255)");
            jdbcTemplate.execute("ALTER TABLE referrals ADD COLUMN IF NOT EXISTS hometown VARCHAR(255)");
            jdbcTemplate.execute("ALTER TABLE referrals ADD COLUMN IF NOT EXISTS stage VARCHAR(255)");

            jdbcTemplate.execute("ALTER TABLE job_postings ADD COLUMN IF NOT EXISTS job_type VARCHAR(255)");
            jdbcTemplate.execute("ALTER TABLE job_postings ADD COLUMN IF NOT EXISTS no_of_openings INTEGER");
            jdbcTemplate.execute("ALTER TABLE job_postings ADD COLUMN IF NOT EXISTS job_responsibility TEXT");
            jdbcTemplate.execute("ALTER TABLE job_postings ADD COLUMN IF NOT EXISTS notice_period VARCHAR(255)");
            jdbcTemplate.execute("ALTER TABLE job_postings ADD COLUMN IF NOT EXISTS ctc_range VARCHAR(255)");
            jdbcTemplate.execute("ALTER TABLE job_postings ADD COLUMN IF NOT EXISTS application_deadline DATE");
            jdbcTemplate.execute("ALTER TABLE job_postings ADD COLUMN IF NOT EXISTS start_time VARCHAR(255)");
            jdbcTemplate.execute("ALTER TABLE job_postings ADD COLUMN IF NOT EXISTS end_time VARCHAR(255)");
            System.out.println("✅ Recruitment columns successfully created.");
        } catch (Exception e) {
            System.out.println("⚡ Error adding recruitment columns: " + e.getMessage());
        }

        // 0. ENSURE ROLES EXIST FIRST
        Role adminRole = getOrCreateRole("ADMIN");
        Role employeeRole = getOrCreateRole("EMPLOYEE");
        Role clientRole = getOrCreateRole("CLIENT");
        Role itAdminRole = getOrCreateRole("IT_ADMIN");
        Role superAdminRole = getOrCreateRole("SUPER_ADMIN");
        Role hrAdminRole = getOrCreateRole("HR_ADMIN");
        Role hrExecutiveRole = getOrCreateRole("HR_EXECUTIVE");
        Role managerRole = getOrCreateRole("MANAGER");
        Role financeRole = getOrCreateRole("FINANCE");
        Role recruiterRole = getOrCreateRole("RECRUITER");
        Role itSupportRole = getOrCreateRole("IT_SUPPORT");
        Role hrManagerRole = getOrCreateRole("HR_MANAGER");
        Role projectManagerRole = getOrCreateRole("PROJECT_MANAGER");
        Role auditorRole = getOrCreateRole("AUDITOR");
        Role transportRole = getOrCreateRole("TRANSPORT");
        Role lndRole = getOrCreateRole("LND");
        Role rewardsRole = getOrCreateRole("REWARDS");
        Role seniorManagerRole = getOrCreateRole("SENIOR_MANAGER");
        Role seniorHrRole = getOrCreateRole("SENIOR_HR");
        Role seniorLndHeadRole = getOrCreateRole("SENIOR_LND_HEAD");
        Role seniorAccountsHeadRole = getOrCreateRole("SENIOR_ACCOUNTS_HEAD");
        Role seniorTransportHeadRole = getOrCreateRole("SENIOR_TRANSPORT_HEAD");
        Role seniorRewardsHeadRole = getOrCreateRole("SENIOR_REWARDS_HEAD");
        Role facilityL2Role = getOrCreateRole("FACILITY_L2");
        Role seniorFacilityHeadRole = getOrCreateRole("SENIOR_FACILITY_HEAD");
        Role seniorItHeadRole = getOrCreateRole("SENIOR_IT_HEAD");

        // ==========================================
        // THE MATRIX MAPPING (Strict 1:1 with BRD)
        // ==========================================
        System.out.println("🔑 Forging Granular Permissions based on RBAC Document...");

        // CORE ROUTING PERMISSION
        Permission adminDashView = getOrCreatePermission("admin_dashboard_view");

        // EMPLOYEE MANAGEMENT
        Permission empView = getOrCreatePermission("employee_view");
        Permission empCreate = getOrCreatePermission("employee_create");
        Permission empEdit = getOrCreatePermission("employee_edit");
        Permission empDelete = getOrCreatePermission("employee_delete");

        // ATTENDANCE & LEAVE
        Permission attView = getOrCreatePermission("attendance_view");
        Permission attMark = getOrCreatePermission("attendance_mark");
        Permission attEdit = getOrCreatePermission("attendance_edit");
        Permission attApprove = getOrCreatePermission("attendance_approve");
        Permission attExport = getOrCreatePermission("attendance_export");
        Permission leaveApply = getOrCreatePermission("leave_apply");
        Permission leaveApprove = getOrCreatePermission("leave_approve");
        Permission leaveView = getOrCreatePermission("leave_view");
        Permission leaveConfig = getOrCreatePermission("leave_configure");

        // PAYROLL & FINANCE
        Permission payrollView = getOrCreatePermission("payroll_view");
        Permission payrollGen = getOrCreatePermission("payroll_generate");
        Permission payrollEdit = getOrCreatePermission("payroll_edit");
        Permission payslipView = getOrCreatePermission("payslip_view");
        Permission payslipDownload = getOrCreatePermission("payslip_download");

        // RECRUITMENT & APPRAISAL
        Permission recPost = getOrCreatePermission("recruitment_postjob");
        Permission recManage = getOrCreatePermission("recruitment_candidate_manage");
        Permission recInterview = getOrCreatePermission("recruitment_interview");
        Permission recOffer = getOrCreatePermission("recruitment_offer");
        Permission appCreate = getOrCreatePermission("appraisal_create");
        Permission appRate = getOrCreatePermission("appraisal_rate");
        Permission appView = getOrCreatePermission("appraisal_view");

        // ASSETS, DOCS & SETTINGS
        Permission assetAdd = getOrCreatePermission("asset_add");
        Permission assetAssign = getOrCreatePermission("asset_assign");
        Permission assetView = getOrCreatePermission("asset_view");
        Permission docUpload = getOrCreatePermission("doc_upload");
        Permission docView = getOrCreatePermission("doc_view");
        Permission docDelete = getOrCreatePermission("doc_delete");
        Permission settingCompany = getOrCreatePermission("settings_manage_company");
        Permission settingHolidays = getOrCreatePermission("settings_manage_holidays");
        Permission settingShifts = getOrCreatePermission("settings_manage_shifts");
        Permission settingRoles = getOrCreatePermission("settings_manage_roles");

        // NEW LEVEL 3 GRANULAR PERMISSIONS
        Permission teamView = getOrCreatePermission("team_view");
        Permission teamEditLimited = getOrCreatePermission("team_edit_limited");
        Permission timesheetApprove = getOrCreatePermission("timesheet_approve");
        Permission goalAssign = getOrCreatePermission("goal_assign");
        Permission recruitmentFeedback = getOrCreatePermission("recruitment_feedback");
        Permission expenseApprove = getOrCreatePermission("expense_approve");
        Permission reportViewTeam = getOrCreatePermission("report_view_team");

        Permission learningStrategyManage = getOrCreatePermission("learning_strategy_manage");
        Permission courseCreate = getOrCreatePermission("course_create");
        Permission courseEdit = getOrCreatePermission("course_edit");
        Permission courseAssign = getOrCreatePermission("course_assign");
        Permission trainingSchedule = getOrCreatePermission("training_schedule");
        Permission skillFrameworkManage = getOrCreatePermission("skill_framework_manage");
        Permission assessmentCreate = getOrCreatePermission("assessment_create");
        Permission certificationIssue = getOrCreatePermission("certification_issue");
        Permission trainingReportView = getOrCreatePermission("training_report_view");
        Permission trainerManage = getOrCreatePermission("trainer_manage");
        Permission learningBudgetManage = getOrCreatePermission("learning_budget_manage");

        Permission glManage = getOrCreatePermission("gl_manage");
        Permission journalEntryCreate = getOrCreatePermission("journal_entry_create");
        Permission journalEntryApprove = getOrCreatePermission("journal_entry_approve");
        Permission payrollApprove = getOrCreatePermission("payroll_approve");
        Permission budgetManage = getOrCreatePermission("budget_manage");
        Permission invoiceCreate = getOrCreatePermission("invoice_create");
        Permission invoiceApprove = getOrCreatePermission("invoice_approve");
        Permission taxManage = getOrCreatePermission("tax_manage");
        Permission financialReportView = getOrCreatePermission("financial_report_view");
        Permission vendorManage = getOrCreatePermission("vendor_manage");
        Permission auditLogView = getOrCreatePermission("audit_log_view");

        Permission fleetManage = getOrCreatePermission("fleet_manage");
        Permission driverManage = getOrCreatePermission("driver_manage");
        Permission routeManage = getOrCreatePermission("route_manage");
        Permission transportAssign = getOrCreatePermission("transport_assign");
        Permission transportApprove = getOrCreatePermission("transport_approve");
        Permission transportExpenseApprove = getOrCreatePermission("transport_expense_approve");
        Permission maintenanceTrack = getOrCreatePermission("maintenance_track");
        Permission gpsTrackView = getOrCreatePermission("gps_track_view");
        Permission safetyIncidentManage = getOrCreatePermission("safety_incident_manage");
        Permission transportReportView = getOrCreatePermission("transport_report_view");

        Permission rewardProgramManage = getOrCreatePermission("reward_program_manage");
        Permission rewardCatalogManage = getOrCreatePermission("reward_catalog_manage");
        Permission pointsAllocate = getOrCreatePermission("points_allocate");
        Permission pointsAdjust = getOrCreatePermission("points_adjust");
        Permission rewardApprove = getOrCreatePermission("reward_approve");
        Permission rewardReportView = getOrCreatePermission("reward_report_view");
        Permission campaignManage = getOrCreatePermission("campaign_manage");
        Permission notificationConfigure = getOrCreatePermission("notification_configure");
        Permission platformConfigure = getOrCreatePermission("platform_configure");

        System.out.println("🔗 Assigning Permissions to Roles...");

        // 1. SUPER ADMIN (Full Access)
        superAdminRole.setPermissions(new HashSet<>(Arrays.asList(
                adminDashView, empView, empCreate, empEdit, empDelete, attView, attMark, attEdit, attApprove, attExport,
                leaveApply, leaveApprove, leaveView, leaveConfig, payrollView, payrollGen, payrollEdit, payslipView, payslipDownload,
                recPost, recManage, recInterview, recOffer, appCreate, appRate, appView, assetAdd, assetAssign, assetView,
                docUpload, docView, docDelete, settingCompany, settingHolidays, settingShifts, settingRoles
        )));
        roleRepository.save(superAdminRole);

        // 2. HR ADMIN (HR Ops, No Payroll Gen, Cannot config roles)
        hrAdminRole.setPermissions(new HashSet<>(Arrays.asList(
                adminDashView, empView, empCreate, empEdit, empDelete, attView, attMark, attEdit, attApprove, attExport,
                leaveApply, leaveApprove, leaveView, leaveConfig, payrollView, payrollEdit, payslipView, payslipDownload,
                recPost, recManage, recInterview, recOffer, appCreate, appRate, appView, assetAssign, assetView, docUpload, docView, docDelete,
                settingHolidays, settingShifts
        )));
        roleRepository.save(hrAdminRole);

        // 3. HR EXECUTIVE (No delete rights, no config rights, can assign assets)
        hrExecutiveRole.setPermissions(new HashSet<>(Arrays.asList(
                adminDashView, empView, empCreate, empEdit, attView, attMark, attEdit, attApprove, attExport,
                leaveApply, leaveApprove, leaveView, payslipView, payslipDownload, appView, assetAssign, assetView, docUpload, docView
        )));
        roleRepository.save(hrExecutiveRole);

        // 4. MANAGER (Team approvals, Interviews, Appraisal Ratings)
        managerRole.setPermissions(new HashSet<>(Arrays.asList(
                adminDashView, empView, attView, attApprove, attExport, leaveApply, leaveApprove, leaveView,
                payslipView, payslipDownload, recInterview, appRate, appView, assetView, docView
        )));
        roleRepository.save(managerRole);

        // 5. FINANCE (Payroll generation, structure edits)
        financeRole.setPermissions(new HashSet<>(Arrays.asList(
                adminDashView, empView, leaveApply, payrollView, payrollGen, payrollEdit, payslipView, payslipDownload
        )));
        roleRepository.save(financeRole);

        // 6. RECRUITER (Job posting, candidates, offers)
        recruiterRole.setPermissions(new HashSet<>(Arrays.asList(
                adminDashView, empView, leaveApply, payslipView, payslipDownload, recPost, recManage, recInterview, recOffer
        )));
        roleRepository.save(recruiterRole);

        // 7. IT ADMIN (Hardware, Systems)
        itAdminRole.setPermissions(new HashSet<>(Arrays.asList(
                adminDashView, empView, leaveApply, payslipView, payslipDownload, assetAdd, assetAssign, assetView, docUpload, docView
        )));
        roleRepository.save(itAdminRole);
        
     // AUDITOR (Read Only)
        auditorRole.setPermissions(new HashSet<>(Arrays.asList(
                adminDashView,
                empView,
                attView,
                leaveView,
                payrollView,
                payslipView,
                appView,
                assetView,
                docView
        )));
        roleRepository.save(auditorRole);

        // IT SUPPORT
        itSupportRole.setPermissions(new HashSet<>(Arrays.asList(
                adminDashView,
                empView,
                assetView,
                assetAssign,
                docView,
                docUpload
        )));
        roleRepository.save(itSupportRole);

        // HR MANAGER
        hrManagerRole.setPermissions(new HashSet<>(Arrays.asList(
                adminDashView,
                empView,
                empCreate,
                empEdit,
                attView,
                attApprove,
                leaveView,
                leaveApprove,
                recPost,
                recManage,
                recInterview,
                recOffer,
                appCreate,
                appRate,
                appView,
                docView,
                docUpload
        )));
        roleRepository.save(hrManagerRole);

        // PROJECT MANAGER
        projectManagerRole.setPermissions(new HashSet<>(Arrays.asList(
                adminDashView,
                empView,
                attView,
                leaveView,
                leaveApprove,
                appView
        )));
        roleRepository.save(projectManagerRole);

        // TRANSPORT
        transportRole.setPermissions(new HashSet<>(Arrays.asList(
                adminDashView,
                empView
        )));
        roleRepository.save(transportRole);

        // LND
        lndRole.setPermissions(new HashSet<>(Arrays.asList(
                adminDashView,
                empView,
                appView,
                docView,
                docUpload
        )));
        roleRepository.save(lndRole);
        
     // SENIOR MANAGER
        seniorManagerRole.setPermissions(new HashSet<>(Arrays.asList(
                adminDashView, empView, empEdit, attView, attApprove, attExport, leaveApply, leaveApprove, leaveView, payslipView, payslipDownload, recInterview, appRate, appView, assetView, docView,
                teamView, teamEditLimited, timesheetApprove, goalAssign, recruitmentFeedback, expenseApprove, reportViewTeam
        )));
        roleRepository.save(seniorManagerRole);

        // SENIOR HR
        seniorHrRole.setPermissions(new HashSet<>(Arrays.asList(
                adminDashView, empView, empCreate, empEdit, empDelete, attView, attMark, attEdit, attApprove, attExport, leaveApply, leaveApprove, leaveView, leaveConfig, payrollView, payrollGen, payrollEdit, payslipView, payslipDownload, recPost, recManage, recInterview, recOffer, appCreate, appRate, appView, assetAssign, assetView, docUpload, docView, docDelete, settingHolidays, settingShifts
        )));
        roleRepository.save(seniorHrRole);

        // SENIOR LND HEAD
        seniorLndHeadRole.setPermissions(new HashSet<>(Arrays.asList(
                adminDashView, empView, appView, appCreate, appRate, docView, docUpload,
                learningStrategyManage, courseCreate, courseEdit, courseAssign, trainingSchedule, skillFrameworkManage, assessmentCreate, certificationIssue, trainingReportView, trainerManage, learningBudgetManage
        )));
        roleRepository.save(seniorLndHeadRole);

        // SENIOR ACCOUNTS HEAD
        seniorAccountsHeadRole.setPermissions(new HashSet<>(Arrays.asList(
                adminDashView, empView, payrollView, payrollGen, payrollEdit, payslipView, payslipDownload,
                glManage, journalEntryCreate, journalEntryApprove, payrollApprove, budgetManage, invoiceCreate, invoiceApprove, taxManage, financialReportView, vendorManage, auditLogView
        )));
        roleRepository.save(seniorAccountsHeadRole);

        // SENIOR TRANSPORT HEAD
        seniorTransportHeadRole.setPermissions(new HashSet<>(Arrays.asList(
                adminDashView, empView,
                fleetManage, driverManage, routeManage, transportAssign, transportApprove, transportExpenseApprove, maintenanceTrack, gpsTrackView, safetyIncidentManage, transportReportView
        )));
        roleRepository.save(seniorTransportHeadRole);

        // SENIOR REWARDS HEAD
        seniorRewardsHeadRole.setPermissions(new HashSet<>(Arrays.asList(
                adminDashView, empView, appView, docView,
                rewardProgramManage, rewardCatalogManage, pointsAllocate, pointsAdjust, rewardApprove, rewardReportView, campaignManage, notificationConfigure, platformConfigure
        )));
        roleRepository.save(seniorRewardsHeadRole);

        // FACILITY L2
        facilityL2Role.setPermissions(new HashSet<>(Arrays.asList(
                adminDashView, empView
        )));
        roleRepository.save(facilityL2Role);

        // SENIOR FACILITY HEAD
        seniorFacilityHeadRole.setPermissions(new HashSet<>(Arrays.asList(
                adminDashView, empView
        )));
        roleRepository.save(seniorFacilityHeadRole);

        // SENIOR IT HEAD
        seniorItHeadRole.setPermissions(new HashSet<>(Arrays.asList(
                adminDashView, empView, assetAdd, assetAssign, assetView, docUpload, docView, docDelete, settingRoles
        )));
        roleRepository.save(seniorItHeadRole);

        // REWARDS
        rewardsRole.setPermissions(new HashSet<>(Arrays.asList(
                adminDashView,
                empView
        )));
        roleRepository.save(rewardsRole);

        // 8. STANDARD EMPLOYEE (Self-Service only, NO adminDashView)
        employeeRole.setPermissions(new HashSet<>(Arrays.asList(
                empView, attMark, leaveApply, leaveView, payslipView, payslipDownload, appView, assetView, docView
        )));
        roleRepository.save(employeeRole);

        // ==========================================
        // THE LEGACY DATA PATCHER
        // ==========================================
        System.out.println("🔍 Scanning for legacy users missing a Role Object...");
        List<User> usersWithoutRoles = userRepository.findAll().stream()
                .filter(u -> u.getRole() == null)
                .toList();

        for (User u : usersWithoutRoles) {
            if (u.getUsername().toUpperCase().startsWith("ADM")) {
                u.setRole(superAdminRole);
            } else if (u.getUsername().toUpperCase().startsWith("CLI")) {
                u.setRole(clientRole);
            } else {
                u.setRole(employeeRole);
            }
            userRepository.save(u);
            System.out.println("🔧 UPGRADED: Assigned Role to legacy user -> " + u.getUsername());
        }

        // OVERRIDE: UPGRADE ADM001 TO SUPER_ADMIN
        Optional<User> admOpt = userRepository.findByUsername("ADM001");
        if (admOpt.isPresent()) {
            User adm = admOpt.get();
            if (adm.getRole() != null && "ADMIN".equals(adm.getRole().getRoleName())) {
                adm.setRole(superAdminRole);
                userRepository.save(adm);
                System.out.println("🚀 Upgraded ADM001 from legacy ADMIN to SUPER_ADMIN!");
            }
        }

        // 1. SEED ADMIN ACCOUNT
        if (userRepository.findByUsername("ADM001").isEmpty()) {
            User admin = new User();
            admin.setUsername("ADM001");
            admin.setFullName("System Administrator");
            admin.setEmail("admin@whitecirclegroup.com");
            admin.setRole(superAdminRole);
            admin.setPassword("{noop}admin123");

            EmployeeProfile adminProfile = new EmployeeProfile();
            adminProfile.setJoiningDate(LocalDate.now());
            adminProfile.setUser(admin);
            admin.setEmployeeProfile(adminProfile);

            userRepository.save(admin);
            System.out.println("✅ Created Admin -> ID: ADM001 | Pass: admin123");
        }

        // 2. SEED CLIENT ACCOUNT (CLI001)
        User cli001User;
        if (userRepository.findByUsername("CLI001").isEmpty()) {
            User client = new User();
            client.setUsername("CLI001");
            client.setFullName("Acme Corp Client");
            client.setEmail("client@acmecorp.com");
            client.setRole(clientRole);
            client.setPassword("{noop}welcome123");
            cli001User = userRepository.save(client);
            System.out.println("✅ Created Client User -> ID: CLI001 | Pass: welcome123");
        } else {
            cli001User = userRepository.findByUsername("CLI001").get();
        }

        // 2.5 ENSURE PROPER 'CLIENT' TABLE RECORD EXISTS FOR CLI001
        if (clientRepository.findByUser_Username("CLI001").isEmpty()) {
            Client clientRecord = new com.example.admindashboard.model.Client();
            clientRecord.setUser(cli001User);
            clientRecord.setClientId("CLI001");
            clientRecord.setCompanyName("Acme Corporation");
            clientRecord.setDomain("Manufacturing & Logistics");
            clientRecord.setContactPerson("Acme Contact");
            clientRecord.setOfficialEmail("client@acmecorp.com");
            clientRecord.setPhoneNumber("+1-800-555-0199");
            clientRecord.setAccountStatus("Active Contract");
            clientRecord.setTeamLead("Rahul Verma");

            clientRepository.save(clientRecord);
            System.out.println("✅ Generated secure Client Table Profile for CLI001");
        }

        // ==========================================
        // REAL EMPLOYEE DATA SEEDING
        // ==========================================

        // Employee 1: Om Tripathi
        if (userRepository.findByUsername("EMP114").isEmpty()) {
            User emp1 = new User();
            emp1.setUsername("EMP114");
            emp1.setPassword("{noop}welcome123");
            emp1.setRole(employeeRole);
            emp1.setFullName("Om Tripathi");
            emp1.setEmail("om.whitecirclegroup@gmail.com");

            EmployeeProfile profile1 = new EmployeeProfile();
            profile1.setDesignation("Marketing Lead");
            profile1.setEmployeeCode("EMP114");
            profile1.setMobileNumber("6265147016");
            profile1.setExperience("05 Years");
            profile1.setJoiningDate(LocalDate.of(2023, 3, 5));
            profile1.setBusinessUnit("Bhopal");
            profile1.setProjectName("Saller");
            profile1.setProjectCode("S65");
            profile1.setReportingManager("Virendra Tiwari");
            profile1.setCustomerName("Manish Rastogi");
            profile1.setWorkLocation("Bhopal");
            profile1.setCity("Bhopal");
            profile1.setBuHrContact("7509759872");
            profile1.setDob(LocalDate.of(1992, 8, 24));

            profile1.setUser(emp1);
            emp1.setEmployeeProfile(profile1);
            userRepository.save(emp1);
            System.out.println("✅ Created Employee -> ID: EMP114 | Name: Om Tripathi");
        }

        // Employee 2: Om Singrore
        if (userRepository.findByUsername("EMP187").isEmpty()) {
            User emp2 = new User();
            emp2.setUsername("EMP187");
            emp2.setPassword("{noop}welcome123");
            emp2.setRole(employeeRole);
            emp2.setFullName("Om Singrore");
            emp2.setEmail("omsingrorewhitecirclegroup@gmail.com");

            EmployeeProfile profile2 = new EmployeeProfile();
            profile2.setDesignation("Project Manager");
            profile2.setDepartment("Product");
            profile2.setMobileNumber("+91 75879 57916");
            profile2.setExperience("3.5 Years");
            profile2.setJoiningDate(LocalDate.of(2022, 5, 7));
            profile2.setBusinessUnit("Bhopal");
            profile2.setProjectName("Index");
            profile2.setProjectCode("IX47");
            profile2.setReportingManager("Sudeep Radhakrishnan");
            profile2.setCustomerName("Neha Tabbu");
            profile2.setWorkLocation("Bhopal");
            profile2.setCity("Bhopal");
            profile2.setBuHrContact("7509759872");
            profile2.setDob(LocalDate.of(1989, 4, 16));

            profile2.setUser(emp2);
            emp2.setEmployeeProfile(profile2);
            userRepository.save(emp2);
            System.out.println("✅ Created Employee -> ID: EMP187 | Name: Om Singrore");
        }

        // Employee 3: Saumya Katare
        if (userRepository.findByUsername("EMP129").isEmpty()) {
            User emp3 = new User();
            emp3.setUsername("EMP129");
            emp3.setPassword("{noop}welcome123");
            emp3.setRole(employeeRole);
            emp3.setFullName("Saumya Katare");
            emp3.setEmail("saumyawhitecirclegroup@gmail.com");

            EmployeeProfile profile3 = new EmployeeProfile();
            profile3.setDesignation("IOS Developer");
            profile3.setEmployeeCode("EMP129");
            profile3.setMobileNumber("+91 7724051300");
            profile3.setExperience("4.8 Years");
            profile3.setJoiningDate(LocalDate.of(2022, 5, 7));
            profile3.setBusinessUnit("Bhopal");
            profile3.setProjectName("Sald");
            profile3.setProjectCode("SD09");
            profile3.setReportingManager("Manikanata");
            profile3.setCustomerName("Gairy Singh Nahar");
            profile3.setWorkLocation("Raipur");
            profile3.setCity("Raipur");
            profile3.setBuHrContact("7509759872");
            profile3.setDob(LocalDate.of(1995, 5, 8));

            profile3.setUser(emp3);
            emp3.setEmployeeProfile(profile3);
            userRepository.save(emp3);
            System.out.println("✅ Created Employee -> ID: EMP129 | Name: Saumya Katare");
        }

        // ==========================================
        // RBAC TESTING ACCOUNTS
        // ==========================================
        
     // Test Account: Admin

        if (userRepository.findByUsername("ADMIN001").isEmpty()) {

            User user = new User();

            user.setUsername("ADMIN001");
            user.setPassword("{noop}Admin@123");
            user.setRole(adminRole);

            user.setFullName("Anil Sharma");
            user.setEmail("admin@wcg.com");

            EmployeeProfile profile = new EmployeeProfile();

            profile.setDesignation("Company Admin / IT Admin");
            profile.setEmployeeCode("ADMIN001");
            profile.setJoiningDate(LocalDate.now());

            profile.setUser(user);
            user.setEmployeeProfile(profile);

            userRepository.save(user);
        }

        // Test Account 1: Senior HR (L3)
        Optional<User> emp201Opt = userRepository.findByUsername("EMP201");
        if (emp201Opt.isEmpty()) {
            User hrUser = new User();
            hrUser.setUsername("EMP201");
            hrUser.setPassword("{noop}welcome123");
            hrUser.setRole(seniorHrRole);
            hrUser.setFullName("Priya Sharma");
            hrUser.setEmail("priya.hr@whitecirclegroup.com");

            EmployeeProfile hrProfile = new EmployeeProfile();
            hrProfile.setDesignation("HR Director");
            hrProfile.setEmployeeCode("EMP201");
            hrProfile.setJoiningDate(LocalDate.of(2021, 1, 15));
            hrProfile.setBusinessUnit("Head Office");

            hrProfile.setUser(hrUser);
            hrUser.setEmployeeProfile(hrProfile);
            userRepository.save(hrUser);
            System.out.println("✅ Created Senior HR -> ID: EMP201");
        } else {
            User hrUser = emp201Opt.get();
            if (hrUser.getRole() == null || !"SENIOR_HR".equals(hrUser.getRole().getRoleName())) {
                hrUser.setRole(seniorHrRole);
                userRepository.save(hrUser);
                System.out.println("✅ Upgraded EMP201 to Senior HR (L3)");
            }
        }

        // Test Account 2: Finance / Payroll
        if (userRepository.findByUsername("EMP301").isEmpty()) {
            User finUser = new User();
            finUser.setUsername("EMP301");
            finUser.setPassword("{noop}welcome123");
            finUser.setRole(financeRole);
            finUser.setFullName("Kavita Finance");
            finUser.setEmail("accounts@wcg.com");

            EmployeeProfile finProfile = new EmployeeProfile();
            finProfile.setDesignation("Payroll Manager");
            finProfile.setJoiningDate(LocalDate.of(2020, 6, 10));
            finProfile.setBusinessUnit("Head Office");

            finProfile.setUser(finUser);
            finUser.setEmployeeProfile(finProfile);
            userRepository.save(finUser);
            System.out.println("✅ Created Finance Admin -> ID: EMP301");
        }

        // Test Account 3: Recruiter
        if (userRepository.findByUsername("EMP401").isEmpty()) {
            User recUser = new User();
            recUser.setUsername("EMP401");
            recUser.setPassword("{noop}welcome123");
            recUser.setRole(recruiterRole);
            recUser.setFullName("Sneha Gupta");
            recUser.setEmail("sneha.talent@whitecirclegroup.com");

            EmployeeProfile recProfile = new EmployeeProfile();
            recProfile.setDesignation("Lead Recruiter");
            recProfile.setJoiningDate(LocalDate.of(2022, 11, 1));
            recProfile.setBusinessUnit("Head Office");

            recProfile.setUser(recUser);
            recUser.setEmployeeProfile(recProfile);
            userRepository.save(recUser);
            System.out.println("✅ Created Recruiter -> ID: EMP401");
        }
        
     // Test Account 4: IT Support
        if (userRepository.findByUsername("EMP501").isEmpty()) {
            User user = new User();
            user.setUsername("EMP501");
            user.setPassword("{noop}welcome123");
            user.setRole(itSupportRole);
            user.setFullName("Ravi IT");
            user.setEmail("it.support@wcg.com");

            EmployeeProfile profile = new EmployeeProfile();
            profile.setDesignation("IT Support");
            profile.setJoiningDate(LocalDate.now());

            profile.setUser(user);
            user.setEmployeeProfile(profile);

            userRepository.save(user);
        }

        // Test Account 5: HR Manager
        if (userRepository.findByUsername("EMP601").isEmpty()) {
            User user = new User();
            user.setUsername("EMP601");
            user.setPassword("{noop}welcome123");
            user.setRole(hrManagerRole);
            user.setFullName("Neha Verma");
            user.setEmail("hr.manager@wcg.com");

            EmployeeProfile profile = new EmployeeProfile();
            profile.setDesignation("HR Manager");
            profile.setJoiningDate(LocalDate.now());

            profile.setUser(user);
            user.setEmployeeProfile(profile);

            userRepository.save(user);
        }

        // Test Account 6: Project Manager
        if (userRepository.findByUsername("EMP701").isEmpty()) {
            User user = new User();
            user.setPassword("{noop}welcome123");
            user.setUsername("EMP701");
            user.setRole(projectManagerRole);
            user.setFullName("Amit Project");
            user.setEmail("pm@wcg.com");

            EmployeeProfile profile = new EmployeeProfile();
            profile.setDesignation("Project Manager");
            profile.setJoiningDate(LocalDate.now());

            profile.setUser(user);
            user.setEmployeeProfile(profile);

            userRepository.save(user);
        }

        // Test Account 7: Auditor
        if (userRepository.findByUsername("EMP801").isEmpty()) {
            User user = new User();
            user.setPassword("{noop}welcome123");
            user.setUsername("EMP801");
            user.setRole(auditorRole);
            user.setFullName("Audit Officer");
            user.setEmail("auditor@wcg.com");

            EmployeeProfile profile = new EmployeeProfile();
            profile.setDesignation("Auditor");
            profile.setJoiningDate(LocalDate.now());

            profile.setUser(user);
            user.setEmployeeProfile(profile);

            userRepository.save(user);
        }

        // Test Account 8: Transport Manager
        if (userRepository.findByUsername("EMP901").isEmpty()) {
            User user = new User();
            user.setPassword("{noop}welcome123");
            user.setUsername("EMP901");
            user.setRole(transportRole);
            user.setFullName("Transport Head");
            user.setEmail("transport@wcg.com");

            EmployeeProfile profile = new EmployeeProfile();
            profile.setDesignation("Transport Manager");
            profile.setJoiningDate(LocalDate.now());

            profile.setUser(user);
            user.setEmployeeProfile(profile);

            userRepository.save(user);
        }

        // Test Account 9: Learning & Development
        if (userRepository.findByUsername("EMP1001").isEmpty()) {
            User user = new User();
            user.setPassword("{noop}welcome123");
            user.setUsername("EMP1001");
            user.setRole(lndRole);
            user.setFullName("Learning Head");
            user.setEmail("learning@wcg.com");

            EmployeeProfile profile = new EmployeeProfile();
            profile.setDesignation("L&D Head");
            profile.setJoiningDate(LocalDate.now());

            profile.setUser(user);
            user.setEmployeeProfile(profile);

            userRepository.save(user);
        }
        
     // Test Account 10: HR Executive
        if (userRepository.findByUsername("EMP1101").isEmpty()) {
            User user = new User();
            user.setUsername("EMP1101");
            user.setPassword("{noop}Admin@123");
            user.setRole(hrExecutiveRole);
            user.setFullName("Pooja Singh");
            user.setEmail("hr.exec@wcg.com");

            EmployeeProfile profile = new EmployeeProfile();
            profile.setDesignation("HR Executive");
            profile.setJoiningDate(LocalDate.now());

            profile.setUser(user);
            user.setEmployeeProfile(profile);

            userRepository.save(user);
        }
     // Test Account 12: Finance
        if (userRepository.findByUsername("EMP1301").isEmpty()) {
            User user = new User();
            user.setUsername("EMP1301");
            user.setPassword("{noop}Admin@123");
            user.setRole(financeRole);
            user.setFullName("Kavita Finance");
            user.setEmail("accounts@wcg.com");

            EmployeeProfile profile = new EmployeeProfile();
            profile.setDesignation("Finance Manager");
            profile.setJoiningDate(LocalDate.now());

            profile.setUser(user);
            user.setEmployeeProfile(profile);

            userRepository.save(user);
        }

        // ==========================================
        // NETWORK HOSPITALS SEEDING
        // ==========================================
        System.out.println("🏥 Checking for Network Hospitals...");
        if (hospitalRepository.count() == 0) {
            System.out.println("⚙️ Seeding dummy network hospitals for testing...");

            Hospital h1 = new Hospital();
            h1.setName("Apollo Hospitals");
            h1.setLocation("Saket, New Delhi");
            h1.setContactNumber("+91-11-29871090");
            h1.setCashless(true);
            h1.setEmergency24x7(true);

            Hospital h2 = new Hospital();
            h2.setName("Fortis Escorts Heart Institute");
            h2.setLocation("Okhla, New Delhi");
            h2.setContactNumber("+91-11-47135000");
            h2.setCashless(true);
            h2.setEmergency24x7(false);

            Hospital h3 = new Hospital();
            h3.setName("Max Super Speciality Hospital");
            h3.setLocation("Vaishali, Ghaziabad");
            h3.setContactNumber("+91-120-4188000");
            h3.setCashless(false);
            h3.setEmergency24x7(true);

            Hospital h4 = new Hospital();
            h4.setName("Orange City Hospital & Research Institute");
            h4.setLocation("Nagpur, Maharashtra");
            h4.setContactNumber("+91-712-6634800");
            h4.setCashless(true);
            h4.setEmergency24x7(true);

            Hospital h5 = new Hospital();
            h5.setName("Bansal Hospital");
            h5.setLocation("Bhopal, Madhya Pradesh");
            h5.setContactNumber("+91-755-4086000");
            h5.setCashless(true);
            h5.setEmergency24x7(false);

            Hospital h6 = new Hospital();
            h6.setName("Care CHL Hospital");
            h6.setLocation("Indore, Madhya Pradesh");
            h6.setContactNumber("+91-731-4774444");
            h6.setCashless(false);
            h6.setEmergency24x7(false);

            hospitalRepository.saveAll(Arrays.asList(h1, h2, h3, h4, h5, h6));
            System.out.println("✅ Successfully seeded 6 Network Hospitals.");
        } else {
            System.out.println("⚡ Network Hospitals already exist. Skipping seed.");
        }

        // ==========================================
        // INSURANCE POLICIES SEEDING (EMP114 & EMP187)
        // ==========================================
        System.out.println("🛡️ Checking for Employee Insurance Policies...");
        if (insurancePolicyRepository.count() == 0) {
            System.out.println("⚙️ Seeding dummy insurance policies...");

            // Seed for EMP114 (Om Tripathi)
            Optional<User> emp114Opt = userRepository.findByUsername("EMP114");
            if (emp114Opt.isPresent()) {
                InsurancePolicy policy1 = new InsurancePolicy();
                policy1.setUser(emp114Opt.get());
                policy1.setPolicyNumber("WCG-2026-MED-114");
                policy1.setProviderName("Star Health & Allied Insurance");
                policy1.setTotalCoverage(500000.0);
                policy1.setAmountUsed(120000.0);
                policy1.setValidFrom(LocalDate.of(2026, 1, 1));
                policy1.setValidUntil(LocalDate.of(2027, 12, 31));
                policy1.setStatus("Active");

                insurancePolicyRepository.save(policy1);
                System.out.println("✅ Assigned Health Policy to EMP114");
            }

            // Seed for EMP187 (Om Singrore)
            Optional<User> emp187Opt = userRepository.findByUsername("EMP187");
            if (emp187Opt.isPresent()) {
                InsurancePolicy policy2 = new InsurancePolicy();
                policy2.setUser(emp187Opt.get());
                policy2.setPolicyNumber("WCG-2026-MED-187");
                policy2.setProviderName("HDFC ERGO General Insurance");
                policy2.setTotalCoverage(750000.0);
                policy2.setAmountUsed(0.0);
                policy2.setValidFrom(LocalDate.of(2026, 4, 1));
                policy2.setValidUntil(LocalDate.of(2027, 3, 31));
                policy2.setStatus("Active");

                insurancePolicyRepository.save(policy2);
                System.out.println("✅ Assigned Health Policy to EMP187");
            }
        } else {
            System.out.println("⚡ Insurance Policies already exist. Skipping seed.");
        }
	
	    /* ==========================================
	       ORGANIZATION HIERARCHY MAPPING
	    ========================================== */
	
	    Optional<User> superAdminOpt =
	            userRepository.findByUsername("ADM001");
	
	    Optional<User> adminOpt =
	            userRepository.findByUsername("ADMIN001");
	
	    Optional<User> hrManagerOpt =
	            userRepository.findByUsername("EMP601");
	
	    Optional<User> hrExecutiveOpt =
	            userRepository.findByUsername("EMP1101");
	
	    Optional<User> itSupportOpt =
	            userRepository.findByUsername("EMP501");
	    Optional<User> itAdminOpt =
	            userRepository.findByUsername("EMP302");
	    Optional<User> managerOpt =
	            userRepository.findByUsername("EMP303");

	    Optional<User> hrAdminOpt =
	            userRepository.findByUsername("EMP201");

	    Optional<User> financeOpt =
	            userRepository.findByUsername("EMP301");

	    Optional<User> finance2Opt =
	            userRepository.findByUsername("EMP1301");

	    Optional<User> recruiterOpt =
	            userRepository.findByUsername("EMP401");

	    Optional<User> projectManagerOpt =
	            userRepository.findByUsername("EMP701");

	    Optional<User> auditorOpt =
	            userRepository.findByUsername("EMP801");

	    Optional<User> transportOpt =
	            userRepository.findByUsername("EMP901");

	    Optional<User> lndOpt =
	            userRepository.findByUsername("EMP1001");
	    
	
	    Optional<User> emp114Opt =
	            userRepository.findByUsername("EMP114");
	
	    Optional<User> emp187Opt =
	            userRepository.findByUsername("EMP187");
	
	    Optional<User> emp129Opt =
	            userRepository.findByUsername("EMP129");
	    Optional<User> surajOpt =
	            userRepository.findByUsername("EMP1010");

	    Optional<User> priyankaOpt =
	            userRepository.findByUsername("EMP119");

	    Optional<User> mishraOpt =
	            userRepository.findByUsername("EMP111");
	
	    /* SUPER_ADMIN -> ADMIN */
	
	    if (superAdminOpt.isPresent() &&
	            adminOpt.isPresent()) {
	
	        User admin = adminOpt.get();
	
	        admin.setManager(
	                superAdminOpt.get()
	        );
	
	        userRepository.save(admin);
	    }
	    
	    /* ADMIN -> ALL DEPARTMENT HEADS */

	    if (adminOpt.isPresent()) {

	        User admin = adminOpt.get();
	        
	        if (hrManagerOpt.isPresent()) {
	            User u = hrManagerOpt.get();
	            u.setManager(admin);
	            userRepository.save(u);
	        }
	        
	        if (hrExecutiveOpt.isPresent()) {
	            User u = hrExecutiveOpt.get();
	            u.setManager(admin);
	            userRepository.save(u);
	        }
	        if (managerOpt.isPresent()) {
	            User u = managerOpt.get();
	            u.setManager(admin);
	            userRepository.save(u);
	        }

	        if (itAdminOpt.isPresent()) {
	            User u = itAdminOpt.get();
	            u.setManager(admin);
	            userRepository.save(u);
	        }

	        if (itSupportOpt.isPresent()) {
	            User u = itSupportOpt.get();
	            u.setManager(admin);
	            userRepository.save(u);
	        }

	        if (hrAdminOpt.isPresent()) {
	            User u = hrAdminOpt.get();
	            u.setManager(admin);
	            userRepository.save(u);
	        }

	        if (financeOpt.isPresent()) {
	            User u = financeOpt.get();
	            u.setManager(admin);
	            userRepository.save(u);
	        }

	        if (finance2Opt.isPresent()) {
	            User u = finance2Opt.get();
	            u.setManager(admin);
	            userRepository.save(u);
	        }

	        if (recruiterOpt.isPresent()) {
	            User u = recruiterOpt.get();
	            u.setManager(admin);
	            userRepository.save(u);
	        }

	        if (projectManagerOpt.isPresent()) {
	            User u = projectManagerOpt.get();
	            u.setManager(admin);
	            userRepository.save(u);
	        }

	        if (auditorOpt.isPresent()) {
	            User u = auditorOpt.get();
	            u.setManager(admin);
	            userRepository.save(u);
	        }

	        if (transportOpt.isPresent()) {
	            User u = transportOpt.get();
	            u.setManager(admin);
	            userRepository.save(u);
	        }

	        if (lndOpt.isPresent()) {
	            User u = lndOpt.get();
	            u.setManager(admin);
	            userRepository.save(u);
	        }
	    }
	
	    /* ADMIN -> HR_MANAGER */
	
	    if (adminOpt.isPresent() &&
	            hrManagerOpt.isPresent()) {
	
	        User hrManager =
	                hrManagerOpt.get();
	
	        hrManager.setManager(
	                adminOpt.get()
	        );
	
	        userRepository.save(hrManager);
	    }
	
	    /* ADMIN -> IT_SUPPORT */
	
	    if (adminOpt.isPresent() &&
	            itSupportOpt.isPresent()) {
	
	        User itSupport =
	                itSupportOpt.get();
	
	        itSupport.setManager(
	                adminOpt.get()
	        );
	
	        userRepository.save(itSupport);
	    }
	
	    /* HR_MANAGER -> HR_EXECUTIVE + EMPLOYEES */
	
	    if (hrManagerOpt.isPresent()) {
	
	        User hrManager =
	                hrManagerOpt.get();
	
	
	        if (emp114Opt.isPresent()) {
	
	            User user =
	                    emp114Opt.get();
	
	            user.setManager(hrManager);
	
	            userRepository.save(user);
	        }
	
	        if (emp187Opt.isPresent()) {
	
	            User user =
	                    emp187Opt.get();
	
	            user.setManager(hrManager);
	
	            userRepository.save(user);
	        }
	
	        if (emp129Opt.isPresent()) {
	
	            User user =
	                    emp129Opt.get();
	
	            user.setManager(hrManager);
	
	            userRepository.save(user);
	        }
	        if (surajOpt.isPresent()) {

	            User user = surajOpt.get();

	            user.setManager(hrManager);

	            userRepository.save(user);
	        }

	        if (priyankaOpt.isPresent()) {

	            User user = priyankaOpt.get();

	            user.setManager(hrManager);

	            userRepository.save(user);
	        }

	        if (mishraOpt.isPresent()) {

	            User user = mishraOpt.get();

	            user.setManager(hrManager);

	            userRepository.save(user);
	        }
	    }
	
	            // Always update existing seeded users to ensure they have correct employeeCode and department
        updateSeededEmployee("EMP114", "Engineering", "Marketing Lead");
        updateSeededEmployee("EMP187", "Product", "Project Manager");
        updateSeededEmployee("EMP129", "Engineering", "IOS Developer");
        updateSeededEmployee("EMP201", "HR", "HR Director");
        updateSeededEmployee("ADMIN001", "IT", "Company Admin / IT Admin");
        updateSeededEmployee("EMP0001", "IT", "Senior IT Head");

        // Ensure proper L2/L3 managers and HRs are assigned to all EmployeeProfiles
        List<User> allSeedUsers = userRepository.findAll();
        for (User u : allSeedUsers) {
            if (u.getRole() != null && "CLIENT".equalsIgnoreCase(u.getRole().getRoleName())) {
                continue;
            }
            EmployeeProfile p = u.getEmployeeProfile();
            if (p == null) {
                p = new EmployeeProfile();
                p.setUser(u);
            }
            
            p.setEmployeeCode(u.getUsername());
            
            // Direct manager (L2 Manager)
            if (u.getManager() != null) {
                p.setReportingManager(u.getManager().getFullName());
                p.setReportsTo(u.getManager().getFullName());
                
                // Senior manager (L3 Manager)
                if (u.getManager().getManager() != null) {
                    p.setDepartmentHead(u.getManager().getManager().getFullName());
                } else {
                    p.setDepartmentHead(u.getManager().getFullName() + " (Direct)");
                }
            } else {
                p.setReportingManager("N/A (Top Management)");
                p.setReportsTo("N/A");
                p.setDepartmentHead("N/A");
            }
            
            // Assign L2 and L3 HR partners
            if ("HR".equalsIgnoreCase(p.getDepartment()) || "Human Resources".equalsIgnoreCase(p.getDepartment())) {
                p.setAssignedHrL2("Priya Sharma");
                p.setAssignedHrL3("Neha Verma");
            } else {
                p.setAssignedHrL2("Pooja Singh");
                p.setAssignedHrL3("Priya Sharma");
            }
            
            u.setEmployeeProfile(p);
            userRepository.save(u);
        }

        System.out.println("=========================================================");
	        
        
    
        // Seed candidate for Onboarding Tab testing
        Role employeeRoleCand = getOrCreateRole("EMPLOYEE");
        if (userRepository.findByUsername("AJITP1972").isEmpty()) {
            User cand = new User();
            cand.setUsername("AJITP1972");
            cand.setPassword("{noop}welcome123");
            cand.setRole(employeeRoleCand);
            cand.setFullName("Ajit Prabhakar");
            cand.setEmail("ajit.prabhakar1972@gmail.com");
            cand.setStatus("ONBOARDING");

            EmployeeProfile profile = new EmployeeProfile();
            profile.setFirstName("Ajit");
            profile.setLastName("Prabhakar");
            profile.setDepartment("Design");
            profile.setDesignation("Senior");
            profile.setWorkLocation("Raipur");
            profile.setJoiningDate(LocalDate.now());
            profile.setMobileNumber("+91 98765 43210");
            profile.setDob(LocalDate.of(1972, 8, 15));
            profile.setUser(cand);
            cand.setEmployeeProfile(profile);

            userRepository.save(cand);
            System.out.println("✅ Seeded Candidate for Onboarding -> Name: Ajit Prabhakar");
        }

    
        // Seed Leave Requests
        if (leaveRequestRepository.count() == 0) {
            seedLeaveRequest("EMP114", "Casual Leave", LocalDate.of(2026, 6, 19), LocalDate.of(2026, 6, 19), 1.0, "Personal Work", "Pending");
            seedLeaveRequest("EMP187", "Annual Leave", LocalDate.of(2026, 6, 20), LocalDate.of(2026, 6, 22), 3.0, "Family Trip", "Pending");
            seedLeaveRequest("EMP129", "Sick Leave", LocalDate.of(2026, 6, 18), LocalDate.of(2026, 6, 18), 1.0, "Fever & Cold", "Approved");
            seedLeaveRequest("EMP114", "Annual Leave", LocalDate.of(2026, 6, 25), LocalDate.of(2026, 6, 29), 5.0, "Vacation", "Pending");
            seedLeaveRequest("EMP187", "Casual Leave", LocalDate.of(2026, 6, 16), LocalDate.of(2026, 6, 16), 1.0, "Personal Work", "Rejected");
            System.out.println("✅ Seeded Leave Requests.");
        }

        // ==========================================
        // SEED FACILITIES ACCOUNTS AND DATA
        // ==========================================
        Role facL2 = getOrCreateRole("FACILITY_L2");
        Role facL3 = getOrCreateRole("SENIOR_FACILITY_HEAD");

        // Clean up old facility_l2/facility_l3 users if present
        userRepository.findByUsername("facility_l2").ifPresent(u -> userRepository.delete(u));
        userRepository.findByUsername("facility_l3").ifPresent(u -> userRepository.delete(u));

        if (userRepository.findByUsername("EMP203").isEmpty()) {
            User user = new User();
            user.setUsername("EMP203");
            user.setPassword("{noop}Welcome@123");
            user.setRole(facL2);
            user.setFullName("Rajesh Kumar");
            user.setEmail("facility.l2@wcg.com");
            user.setDesignation("Facilities Admin");

            EmployeeProfile profile = new EmployeeProfile();
            profile.setDesignation("Facilities Admin");
            profile.setDepartment("Facilities");
            profile.setJoiningDate(LocalDate.now());
            profile.setWorkLocation("Delhi");
            profile.setEmployeeCode("EMP203");

            profile.setUser(user);
            user.setEmployeeProfile(profile);

            userRepository.save(user);
            System.out.println("✅ Seeded user: EMP203");
        }

        if (userRepository.findByUsername("ADMIN105").isEmpty()) {
            User user = new User();
            user.setUsername("ADMIN105");
            user.setPassword("{noop}Welcome@123");
            user.setRole(facL3);
            user.setFullName("Sanjay Singh");
            user.setEmail("facility.l3@wcg.com");
            user.setDesignation("Facility Head");

            EmployeeProfile profile = new EmployeeProfile();
            profile.setDesignation("Facility Head");
            profile.setDepartment("Facilities");
            profile.setJoiningDate(LocalDate.now());
            profile.setWorkLocation("Delhi");
            profile.setEmployeeCode("ADMIN105");

            profile.setUser(user);
            user.setEmployeeProfile(profile);

            userRepository.save(user);
            System.out.println("✅ Seeded user: ADMIN105");
        }

        Role itL3 = getOrCreateRole("SENIOR_IT_HEAD");
        User userIt = userRepository.findByUsername("EMP0001").orElse(null);
        if (userIt == null) {
            userIt = new User();
            userIt.setUsername("EMP0001");
        }
        userIt.setPassword("{noop}Welcome@123");
        userIt.setRole(itL3);
        userIt.setFullName("Rajesh Kumar");
        userIt.setEmail("it.head@wcg.com");
        userIt.setDesignation("Senior IT Head");

        EmployeeProfile profileIt = userIt.getEmployeeProfile();
        if (profileIt == null) {
            profileIt = new EmployeeProfile();
            profileIt.setUser(userIt);
            userIt.setEmployeeProfile(profileIt);
        }
        profileIt.setDesignation("Senior IT Head");
        profileIt.setDepartment("IT");
        profileIt.setJoiningDate(LocalDate.now());
        profileIt.setWorkLocation("Delhi");
        profileIt.setEmployeeCode("EMP0001");

        userRepository.save(userIt);
        System.out.println("✅ Seeded/Updated user: EMP0001");

        Role rewardsL2Role = getOrCreateRole("REWARDS");
        Role rewardsL3Role = getOrCreateRole("SENIOR_REWARDS_HEAD");

        if (userRepository.findByUsername("EMP1002").isEmpty()) {
            User user = new User();
            user.setUsername("EMP1002");
            user.setPassword("{noop}Welcome@123");
            user.setRole(rewardsL2Role);
            user.setFullName("Neha Sharma");
            user.setEmail("rewards.l2@wcg.com");
            user.setDesignation("Rewards Manager");

            EmployeeProfile profile = new EmployeeProfile();
            profile.setDesignation("Rewards Manager");
            profile.setDepartment("Rewards");
            profile.setJoiningDate(LocalDate.now());
            profile.setWorkLocation("Raipur");
            profile.setEmployeeCode("EMP1002");

            profile.setUser(user);
            user.setEmployeeProfile(profile);

            userRepository.save(user);
            System.out.println("✅ Seeded user: EMP1002 (Rewards Manager)");
        }

        if (userRepository.findByUsername("EMP0007").isEmpty()) {
            User user = new User();
            user.setUsername("EMP0007");
            user.setPassword("{noop}Welcome@123");
            user.setRole(rewardsL3Role);
            user.setFullName("Rohit Sharma");
            user.setEmail("rewards.adminwcg@gmail.com");
            user.setDesignation("Rewards Admin");

            EmployeeProfile profile = new EmployeeProfile();
            profile.setDesignation("Rewards Admin");
            profile.setDepartment("Rewards");
            profile.setJoiningDate(LocalDate.now());
            profile.setWorkLocation("Delhi");
            profile.setEmployeeCode("EMP0007");

            profile.setUser(user);
            user.setEmployeeProfile(profile);

            userRepository.save(user);
            System.out.println("✅ Seeded user: EMP0007 (Rewards Head)");
        }

        // Seed Reward Merchandise
        if (rewardMerchandiseRepository.count() == 0) {
            RewardMerchandise m1 = new RewardMerchandise();
            m1.setItemName("WCG T-Shirt");
            m1.setCategory("Apparel");
            m1.setBrand("WCG Custom");
            m1.setDescription("Premium quality white cotton t-shirt with WhiteCircle logo.");
            m1.setRedemptionPoints(600);
            m1.setTotalStocks(15);
            m1.setPerUserLimit(1);
            m1.setImagePath("/images/wcg-tshirt.png");
            m1.setStatus("PUBLISHED");
            rewardMerchandiseRepository.save(m1);

            RewardMerchandise m2 = new RewardMerchandise();
            m2.setItemName("Wireless Headphones");
            m2.setCategory("Audio");
            m2.setBrand("Boat");
            m2.setDescription("High quality wireless noise cancelling over-ear headphones.");
            m2.setRedemptionPoints(600);
            m2.setTotalStocks(10);
            m2.setPerUserLimit(1);
            m2.setImagePath("/images/boat-headphones.png");
            m2.setStatus("PUBLISHED");
            rewardMerchandiseRepository.save(m2);

            RewardMerchandise m3 = new RewardMerchandise();
            m3.setItemName("Amazon Gift Card Rs. 500");
            m3.setCategory("Gift Cards");
            m3.setBrand("Amazon");
            m3.setDescription("E-Gift voucher worth Rs. 500 redeemable on Amazon India.");
            m3.setRedemptionPoints(500);
            m3.setTotalStocks(50);
            m3.setPerUserLimit(5);
            m3.setImagePath("/images/amazon-giftcard.png");
            m3.setStatus("PUBLISHED");
            rewardMerchandiseRepository.save(m3);

            RewardMerchandise m4 = new RewardMerchandise();
            m4.setItemName("AWS Cloud Practitioner Exam Voucher");
            m4.setCategory("Certificates");
            m4.setBrand("AWS");
            m4.setDescription("100% discount voucher for AWS Cloud Practitioner certification exam.");
            m4.setRedemptionPoints(1200);
            m4.setTotalStocks(5);
            m4.setPerUserLimit(1);
            m4.setImagePath("/images/aws-voucher.png");
            m4.setStatus("PUBLISHED");
            rewardMerchandiseRepository.save(m4);
        }

        // Seed Reward Budgets
        if (rewardBudgetRepository.count() == 0) {
            RewardBudget b1 = new RewardBudget();
            b1.setDepartment("Engineering");
            b1.setAllocatedPoints(50000);
            b1.setSpentPoints(12500);
            b1.setFiscalYear("2026-27");
            rewardBudgetRepository.save(b1);

            RewardBudget b2 = new RewardBudget();
            b2.setDepartment("Product");
            b2.setAllocatedPoints(30000);
            b2.setSpentPoints(8400);
            b2.setFiscalYear("2026-27");
            rewardBudgetRepository.save(b2);

            RewardBudget b3 = new RewardBudget();
            b3.setDepartment("HR");
            b3.setAllocatedPoints(20000);
            b3.setSpentPoints(4500);
            b3.setFiscalYear("2026-27");
            rewardBudgetRepository.save(b3);
        }

        // Seed Reward Programs
        if (rewardProgramRepository.count() <= 3) {
            rewardProgramRepository.deleteAll();
            // PEER_TO_PEER
            RewardProgram p1 = new RewardProgram();
            p1.setProgramName("High Five Recognition");
            p1.setProgramType("PEER_TO_PEER");
            p1.setAwardCategory("Peer to peer thanks");
            p1.setDescription("Recognition mechanism for general peer collaboration.");
            p1.setEligibility("All Employees");
            p1.setPointsValue(50);
            p1.setStatus("ACTIVE");
            p1.setValidTill(LocalDate.of(2026, 12, 31));
            p1.setProgramOwner("HR Team");
            rewardProgramRepository.save(p1);

            RewardProgram p2 = new RewardProgram();
            p2.setProgramName("Team Collaborator");
            p2.setProgramType("PEER_TO_PEER");
            p2.setAwardCategory("Team thanks");
            p2.setDescription("Simple thanks award for support between cross-functional teams.");
            p2.setEligibility("All Employees");
            p2.setPointsValue(100);
            p2.setStatus("ACTIVE");
            p2.setValidTill(LocalDate.of(2026, 12, 31));
            p2.setProgramOwner("HR Team");
            rewardProgramRepository.save(p2);

            RewardProgram p3 = new RewardProgram();
            p3.setProgramName("Above & Beyond");
            p3.setProgramType("PEER_TO_PEER");
            p3.setAwardCategory("Peer to peer extra effort");
            p3.setDescription("Peer award acknowledging exceptional work beyond the core job description.");
            p3.setEligibility("All Employees");
            p3.setPointsValue(200);
            p3.setStatus("INACTIVE");
            p3.setValidTill(LocalDate.of(2026, 6, 30));
            p3.setProgramOwner("HR Team");
            rewardProgramRepository.save(p3);

            RewardProgram p4 = new RewardProgram();
            p4.setProgramName("New Joiner Welcome");
            p4.setProgramType("PEER_TO_PEER");
            p4.setAwardCategory("Peer to peer welcome");
            p4.setDescription("Welcome gift of points for joining the organization.");
            p4.setEligibility("All Employees");
            p4.setPointsValue(50);
            p4.setStatus("ACTIVE");
            p4.setValidTill(LocalDate.of(2026, 12, 31));
            p4.setProgramOwner("HR Team");
            rewardProgramRepository.save(p4);

            // SPOT
            RewardProgram s1 = new RewardProgram();
            s1.setProgramName("Quick Thanks");
            s1.setProgramType("SPOT");
            s1.setAwardCategory("Spot");
            s1.setDescription("Instant recognition for resolving critical issues immediately.");
            s1.setEligibility("All Employees");
            s1.setPointsValue(50);
            s1.setStatus("ACTIVE");
            s1.setValidTill(LocalDate.of(2026, 12, 31));
            s1.setProgramOwner("HR Team");
            rewardProgramRepository.save(s1);

            RewardProgram s2 = new RewardProgram();
            s2.setProgramName("Bravo Spot");
            s2.setProgramType("SPOT");
            s2.setAwardCategory("Spot");
            s2.setDescription("Supervisor award for standout performance on a key deliverable.");
            s2.setEligibility("All Employees");
            s2.setPointsValue(150);
            s2.setStatus("ACTIVE");
            s2.setValidTill(LocalDate.of(2026, 12, 31));
            s2.setProgramOwner("HR Team");
            rewardProgramRepository.save(s2);

            RewardProgram s3 = new RewardProgram();
            s3.setProgramName("Star Performer");
            s3.setProgramType("SPOT");
            s3.setAwardCategory("Spot");
            s3.setDescription("Awarded to outstanding stars of the month.");
            s3.setEligibility("All Employees");
            s3.setPointsValue(500);
            s3.setStatus("ACTIVE");
            s3.setValidTill(LocalDate.of(2026, 3, 31));
            s3.setProgramOwner("HR Team");
            rewardProgramRepository.save(s3);

            RewardProgram s4 = new RewardProgram();
            s4.setProgramName("Exceptional Effort");
            s4.setProgramType("SPOT");
            s4.setAwardCategory("Spot");
            s4.setDescription("Acknowledge extraordinary effort to keep clients happy.");
            s4.setEligibility("Customer Support Team");
            s4.setPointsValue(300);
            s4.setStatus("INACTIVE");
            s4.setValidTill(LocalDate.of(2026, 8, 31));
            s4.setProgramOwner("CS Team");
            rewardProgramRepository.save(s4);

            // PERFORMANCE
            RewardProgram pf1 = new RewardProgram();
            pf1.setProgramName("Top Performer Award");
            pf1.setProgramType("PERFORMANCE");
            pf1.setCriteriaType("Rating");
            pf1.setCriteriaValue("4.5+ Rating");
            pf1.setEligibility("High Performers (L1/L2)");
            pf1.setDepartment("All");
            pf1.setPointsValue(1000);
            pf1.setStatus("ACTIVE");
            pf1.setProgramOwner("HR Team");
            rewardProgramRepository.save(pf1);

            RewardProgram pf2 = new RewardProgram();
            pf2.setProgramName("Sales Achievement");
            pf2.setProgramType("PERFORMANCE");
            pf2.setCriteriaType("Achievement");
            pf2.setCriteriaValue("Sales Target Met");
            pf2.setEligibility("Sales Team");
            pf2.setDepartment("Sales");
            pf2.setPointsValue(800);
            pf2.setStatus("ACTIVE");
            pf2.setProgramOwner("Sales Manager");
            rewardProgramRepository.save(pf2);

            RewardProgram pf3 = new RewardProgram();
            pf3.setProgramName("Quarterly Innovation");
            pf3.setProgramType("PERFORMANCE");
            pf3.setCriteriaType("Rating");
            pf3.setCriteriaValue("Innovative Project Approved");
            pf3.setEligibility("All Employees");
            pf3.setDepartment("Engineering");
            pf3.setPointsValue(1500);
            pf3.setStatus("ACTIVE");
            pf3.setProgramOwner("Tech Lead");
            rewardProgramRepository.save(pf3);

            RewardProgram pf4 = new RewardProgram();
            pf4.setProgramName("Team Champion Award");
            pf4.setProgramType("PERFORMANCE");
            pf4.setCriteriaType("Achievement");
            pf4.setCriteriaValue("Outstanding Team Contribution");
            pf4.setEligibility("Dev Team");
            pf4.setDepartment("Engineering");
            pf4.setPointsValue(1200);
            pf4.setStatus("INACTIVE");
            pf4.setProgramOwner("Engineering Head");
            rewardProgramRepository.save(pf4);

            // MILESTONE
            RewardProgram m1 = new RewardProgram();
            m1.setProgramName("Work Anniversary");
            m1.setProgramType("MILESTONE");
            m1.setMilestoneType("Work Anniversary");
            m1.setMilestoneValue("Per Year");
            m1.setEligibility("All Employees");
            m1.setPointsValue(500);
            m1.setStatus("ACTIVE");
            m1.setProgramOwner("HR Team");
            rewardProgramRepository.save(m1);

            RewardProgram m2 = new RewardProgram();
            m2.setProgramName("Birthday Special");
            m2.setProgramType("MILESTONE");
            m2.setMilestoneType("Birthday");
            m2.setMilestoneValue("Per Year");
            m2.setEligibility("All Employees");
            m2.setPointsValue(200);
            m2.setStatus("ACTIVE");
            m2.setProgramOwner("HR Team");
            rewardProgramRepository.save(m2);

            RewardProgram m3 = new RewardProgram();
            m3.setProgramName("5 Years Milestone");
            m3.setProgramType("MILESTONE");
            m3.setMilestoneType("Work Anniversary");
            m3.setMilestoneValue("5 Years");
            m3.setEligibility("Employees with 5 years tenure");
            m3.setPointsValue(2000);
            m3.setStatus("ACTIVE");
            m3.setProgramOwner("HR Team");
            rewardProgramRepository.save(m3);

            RewardProgram m4 = new RewardProgram();
            m4.setProgramName("10 Years Milestone");
            m4.setProgramType("MILESTONE");
            m4.setMilestoneType("Work Anniversary");
            m4.setMilestoneValue("10 Years");
            m4.setEligibility("Employees with 10 years tenure");
            m4.setPointsValue(5000);
            m4.setStatus("ACTIVE");
            m4.setProgramOwner("HR Team");
            rewardProgramRepository.save(m4);

            RewardProgram m5 = new RewardProgram();
            m5.setProgramName("1 Year Milestone");
            m5.setProgramType("MILESTONE");
            m5.setMilestoneType("Work Anniversary");
            m5.setMilestoneValue("1 Year");
            m5.setEligibility("Employees with 1 year tenure");
            m5.setPointsValue(1000);
            m5.setStatus("ACTIVE");
            m5.setProgramOwner("HR Team");
            rewardProgramRepository.save(m5);
        }

        // Seed Reward ServiceRequests (tickets) if not present
        if (serviceRequestRepository.findAll().stream().noneMatch(t -> "REWARDS".equalsIgnoreCase(t.getType()))) {
            seedRewardsTicket("T-101", "EMP114", "Om Tripathi", "Redemption Store", "WCG T-shirt", 600, "Open", "Redeemed WCG T-shirt", "Sector 30, Raigarh, Chhattisgarh");
            seedRewardsTicket("T-102", "EMP187", "Om Singrore", "Points Transfer", "Peer Points Transfer", 30, "Open", "Send points to Saumya Katare", "N/A");
            seedRewardsTicket("T-103", "EMP129", "Saumya Katare", "Gift Cards", "Amazon Gift card Rs. 500", 500, "Open", "Amazon Gift card Rs. 500", "N/A");
            seedRewardsTicket("T-104", "EMP114", "Om Tripathi", "Certificate Rewards", "AWS Practitioner Voucher", 1200, "Rejected", "Completed AWS Cloud Practitioner certification exam", "N/A");
            seedRewardsTicket("T-105", "EMP187", "Om Singrore", "Points Transfer", "Peer Points Transfer", 25, "Approved", "Received from Amit Kumar", "N/A");
        }

        // Seed Reward Rules
        if (rewardRuleRepository.count() == 0) {
            com.example.admindashboard.model.RewardRule r1 = new com.example.admindashboard.model.RewardRule();
            r1.setRuleName("Peer-to-Peer Rule");
            r1.setRuleType("Peer recognition");
            r1.setDescription("Daily/weekly peers appreciation allocations");
            r1.setPointsPerTransaction(50);
            r1.setTargetGroup("All Employees");
            r1.setStatus("ACTIVE");
            r1.setStartDate(LocalDate.of(2026, 1, 1));
            rewardRuleRepository.save(r1);

            com.example.admindashboard.model.RewardRule r2 = new com.example.admindashboard.model.RewardRule();
            r2.setRuleName("Milestone Service Reward");
            r2.setRuleType("Milestone");
            r2.setDescription("5 year and 10 year service milestone awards");
            r2.setPointsPerTransaction(1000);
            r2.setTargetGroup("Specific Employees");
            r2.setStatus("ACTIVE");
            r2.setStartDate(LocalDate.of(2026, 1, 1));
            rewardRuleRepository.save(r2);
        }

        // Seed Team Point Allocations
        if (teamPointAllocationRepository.count() == 0) {
            com.example.admindashboard.model.TeamPointAllocation ta1 = new com.example.admindashboard.model.TeamPointAllocation();
            ta1.setTeamName("Engineering Team");
            ta1.setAllocatedPoints(20000);
            ta1.setDistributedPoints(12000);
            ta1.setRemainingPoints(8000);
            ta1.setActionDate(LocalDate.of(2026, 11, 12));
            ta1.setDescription("Q4 developer recognition allocation");
            teamPointAllocationRepository.save(ta1);

            com.example.admindashboard.model.TeamPointAllocation ta2 = new com.example.admindashboard.model.TeamPointAllocation();
            ta2.setTeamName("Marketing Team");
            ta2.setAllocatedPoints(15000);
            ta2.setDistributedPoints(10000);
            ta2.setRemainingPoints(5000);
            ta2.setActionDate(LocalDate.of(2026, 11, 18));
            ta2.setDescription("Campaign success appreciation allocation");
            teamPointAllocationRepository.save(ta2);

            com.example.admindashboard.model.TeamPointAllocation ta3 = new com.example.admindashboard.model.TeamPointAllocation();
            ta3.setTeamName("Sales Team");
            ta3.setAllocatedPoints(30000);
            ta3.setDistributedPoints(22000);
            ta3.setRemainingPoints(8000);
            ta3.setActionDate(LocalDate.of(2026, 12, 5));
            ta3.setDescription("Q4 target achievement allocation");
            teamPointAllocationRepository.save(ta3);
        }

        // Seed Budget Activities
        if (budgetActivityRepository.count() == 0) {
            com.example.admindashboard.model.BudgetActivity ba1 = new com.example.admindashboard.model.BudgetActivity();
            ba1.setActivityName("Engineering budget allocation");
            ba1.setDepartment("Engineering");
            ba1.setPointsValue(40000);
            ba1.setActivityType("Allocation");
            ba1.setActionDate(LocalDate.of(2026, 11, 10));
            ba1.setStatus("APPROVED");
            budgetActivityRepository.save(ba1);

            com.example.admindashboard.model.BudgetActivity ba2 = new com.example.admindashboard.model.BudgetActivity();
            ba2.setActivityName("QA Department allocation");
            ba2.setDepartment("QA");
            ba2.setPointsValue(15000);
            ba2.setActivityType("Allocation");
            ba2.setActionDate(LocalDate.of(2026, 11, 12));
            ba2.setStatus("APPROVED");
            budgetActivityRepository.save(ba2);

            com.example.admindashboard.model.BudgetActivity ba3 = new com.example.admindashboard.model.BudgetActivity();
            ba3.setActivityName("Amazon Vouchers spent");
            ba3.setDepartment("HR");
            ba3.setPointsValue(5000);
            ba3.setActivityType("Spending");
            ba3.setActionDate(LocalDate.of(2026, 11, 18));
            ba3.setStatus("APPROVED");
            budgetActivityRepository.save(ba3);

            com.example.admindashboard.model.BudgetActivity ba4 = new com.example.admindashboard.model.BudgetActivity();
            ba4.setActivityName("Team Champion spending");
            ba4.setDepartment("Engineering");
            ba4.setPointsValue(1200);
            ba4.setActivityType("Spending");
            ba4.setActionDate(LocalDate.of(2026, 11, 20));
            ba4.setStatus("APPROVED");
            budgetActivityRepository.save(ba4);

            com.example.admindashboard.model.BudgetActivity ba5 = new com.example.admindashboard.model.BudgetActivity();
            ba5.setActivityName("L3 Special Request");
            ba5.setDepartment("Sales");
            ba5.setPointsValue(10000);
            ba5.setActivityType("Allocation");
            ba5.setActionDate(LocalDate.of(2026, 12, 5));
            ba5.setStatus("PENDING");
            budgetActivityRepository.save(ba5);
        }

        // Seed Facility Vendors
        if (facilityVendorRepository.count() == 0) {
            FacilityVendor v1 = new FacilityVendor();
            v1.setName("CleanAir Solutions");
            v1.setCategory("Maintenance");
            v1.setContactPerson("Amit Patel");
            v1.setPhone("+91-9876543210");
            v1.setEmail("amit@coolair.com");
            v1.setStatus("Active");
            v1.setJoiningDate(LocalDate.of(2025, 5, 12));
            facilityVendorRepository.save(v1);

            FacilityVendor v2 = new FacilityVendor();
            v2.setName("Sparkle Clean Services");
            v2.setCategory("Housekeeping");
            v2.setContactPerson("Priya Sharma");
            v2.setPhone("+91-9876543211");
            v2.setEmail("priya@sparkle.com");
            v2.setStatus("Active");
            v2.setJoiningDate(LocalDate.of(2025, 6, 1));
            facilityVendorRepository.save(v2);

            FacilityVendor v3 = new FacilityVendor();
            v3.setName("PowerFix Engineers");
            v3.setCategory("Maintenance");
            v3.setContactPerson("Rajiv Mehta");
            v3.setPhone("+91-9876543212");
            v3.setEmail("rajiv@powerfix.com");
            v3.setStatus("Active");
            v3.setJoiningDate(LocalDate.of(2025, 7, 15));
            facilityVendorRepository.save(v3);

            FacilityVendor v4 = new FacilityVendor();
            v4.setName("Aqua Care Services");
            v4.setCategory("Maintenance");
            v4.setContactPerson("Siddharth Jain");
            v4.setPhone("+91-9876543213");
            v4.setEmail("sid@aquacare.com");
            v4.setStatus("Active");
            v4.setJoiningDate(LocalDate.of(2025, 8, 10));
            facilityVendorRepository.save(v4);

            FacilityVendor v5 = new FacilityVendor();
            v5.setName("Intercontinental Corp");
            v5.setCategory("AMC");
            v5.setContactPerson("David Miller");
            v5.setPhone("+91-9876543214");
            v5.setEmail("david@intercontinental.com");
            v5.setStatus("Active");
            v5.setJoiningDate(LocalDate.of(2025, 9, 20));
            facilityVendorRepository.save(v5);

            System.out.println("✅ Seeded Facility Vendors.");
        }

        // Seed Facility Devices
        if (facilityDeviceRepository.count() == 0) {
            FacilityDevice d1 = new FacilityDevice();
            d1.setDeviceName("AC Preventive Maintenance");
            d1.setDeviceModel("Split AC 2 Ton");
            d1.setVendorName("CleanAir Solutions");
            d1.setSerialNumber("AC-SPL-001");
            d1.setCost(45000.0);
            d1.setWarrantyExpiry(LocalDate.of(2028, 12, 31));
            d1.setPurchaseDate(LocalDate.of(2025, 1, 10));
            d1.setLocation("L3, Block A");
            d1.setIpAddress("192.168.1.50");
            d1.setMacAddress("00:1A:2B:3C:4D:5E");
            d1.setStatus("Active");
            facilityDeviceRepository.save(d1);

            FacilityDevice d2 = new FacilityDevice();
            d2.setDeviceName("Electrical Panel Check");
            d2.setDeviceModel("Main Distribution Board");
            d2.setVendorName("Sparkle Clean Services");
            d2.setSerialNumber("EP-MDB-002");
            d2.setCost(85000.0);
            d2.setWarrantyExpiry(LocalDate.of(2030, 5, 20));
            d2.setPurchaseDate(LocalDate.of(2025, 2, 15));
            d2.setLocation("Basement, Block B");
            d2.setIpAddress("");
            d2.setMacAddress("");
            d2.setStatus("Active");
            facilityDeviceRepository.save(d2);

            FacilityDevice d3 = new FacilityDevice();
            d3.setDeviceName("Generator Servicing");
            d3.setDeviceModel("250 kVA Genset");
            d3.setVendorName("PowerFix Engineers");
            d3.setSerialNumber("GEN-250-003");
            d3.setCost(15000.0);
            d3.setWarrantyExpiry(LocalDate.of(2027, 7, 15));
            d3.setPurchaseDate(LocalDate.of(2025, 3, 20));
            d3.setLocation("Power House, Yard");
            d3.setIpAddress("");
            d3.setMacAddress("");
            d3.setStatus("Active");
            facilityDeviceRepository.save(d3);

            FacilityDevice d4 = new FacilityDevice();
            d4.setDeviceName("Plumbing Inspection");
            d4.setDeviceModel("Water Filtration System");
            d4.setVendorName("Aqua Care Services");
            d4.setSerialNumber("PL-WFS-004");
            d4.setCost(35000.0);
            d4.setWarrantyExpiry(LocalDate.of(2026, 8, 10));
            d4.setPurchaseDate(LocalDate.of(2025, 4, 10));
            d4.setLocation("Terrace, Block C");
            d4.setIpAddress("");
            d4.setMacAddress("");
            d4.setStatus("Active");
            facilityDeviceRepository.save(d4);

            System.out.println("✅ Seeded Facility Devices.");
        }

        // Seed Facility Services
        if (facilityServiceRepository.count() == 0) {
            FacilityService s1 = new FacilityService();
            s1.setServiceName("AC Preventive Maintenance");
            s1.setCategory("Maintenance");
            s1.setVendorName("CleanAir Solutions");
            s1.setCost(12000.0);
            s1.setDescription("All AC Units");
            s1.setStartDate(LocalDate.of(2026, 5, 12));
            s1.setEndDate(LocalDate.of(2027, 5, 12));
            s1.setNextBillDate(LocalDate.of(2026, 6, 12));
            s1.setStatus("Active");
            s1.setFrequency("Monthly");
            s1.setAssignedTo("Rajesh Kumar");
            s1.setPriority("Medium");
            facilityServiceRepository.save(s1);

            FacilityService s2 = new FacilityService();
            s2.setServiceName("Electrical Panel Check");
            s2.setCategory("Maintenance");
            s2.setVendorName("Sparkle Clean Services");
            s2.setCost(45000.0);
            s2.setDescription("Main Building");
            s2.setStartDate(LocalDate.of(2026, 5, 20));
            s2.setEndDate(LocalDate.of(2027, 5, 20));
            s2.setNextBillDate(LocalDate.of(2026, 8, 20));
            s2.setStatus("Active");
            s2.setFrequency("Quarterly");
            s2.setAssignedTo("Rajesh Kumar");
            s2.setPriority("High");
            facilityServiceRepository.save(s2);

            FacilityService s3 = new FacilityService();
            s3.setServiceName("Generator Servicing");
            s3.setCategory("Maintenance");
            s3.setVendorName("PowerFix Engineers");
            s3.setCost(15000.0);
            s3.setDescription("Backup Generator");
            s3.setStartDate(LocalDate.of(2026, 5, 15));
            s3.setEndDate(LocalDate.of(2027, 5, 15));
            s3.setNextBillDate(LocalDate.of(2026, 6, 15));
            s3.setStatus("Active");
            s3.setFrequency("Monthly");
            s3.setAssignedTo("Rajesh Kumar");
            s3.setPriority("Medium");
            facilityServiceRepository.save(s3);

            FacilityService s4 = new FacilityService();
            s4.setServiceName("Plumbing Inspection");
            s4.setCategory("Maintenance");
            s4.setVendorName("Aqua Care Services");
            s4.setCost(5000.0);
            s4.setDescription("All Washrooms");
            s4.setStartDate(LocalDate.of(2026, 5, 8));
            s4.setEndDate(LocalDate.of(2027, 5, 8));
            s4.setNextBillDate(LocalDate.of(2026, 5, 15));
            s4.setStatus("On Call");
            s4.setFrequency("Weekly");
            s4.setAssignedTo("Rajesh Kumar");
            s4.setPriority("Medium");
            facilityServiceRepository.save(s4);

            System.out.println("✅ Seeded Facility Services.");
        }

        // Seed Facility Contracts
        if (facilityContractRepository.count() == 0) {
            FacilityContract c1 = new FacilityContract();
            c1.setContractId("CON-101");
            c1.setContractName("HVAC Maintenance AMC");
            c1.setVendorName("CleanAir Solutions");
            c1.setCategory("Maintenance");
            c1.setStartDate(LocalDate.of(2026, 1, 1));
            c1.setEndDate(LocalDate.of(2026, 12, 31));
            c1.setNextBillDate(LocalDate.of(2026, 2, 1));
            c1.setCost(120000.0);
            c1.setStatus("Active");
            c1.setDescription("Annual Maintenance Contract for all air conditioning units.");
            facilityContractRepository.save(c1);

            FacilityContract c2 = new FacilityContract();
            c2.setContractId("CON-102");
            c2.setContractName("Daily Office Cleaning");
            c2.setVendorName("Sparkle Clean Services");
            c2.setCategory("Housekeeping");
            c2.setStartDate(LocalDate.of(2026, 2, 1));
            c2.setEndDate(LocalDate.of(2027, 1, 31));
            c2.setNextBillDate(LocalDate.of(2026, 3, 1));
            c2.setCost(45000.0);
            c2.setStatus("Active");
            c2.setDescription("Daily cleaning services contract.");
            facilityContractRepository.save(c2);

            FacilityContract c3 = new FacilityContract();
            c3.setContractId("CON-103");
            c3.setContractName("Main Building Security");
            c3.setVendorName("Intercontinental Corp");
            c3.setCategory("Security");
            c3.setStartDate(LocalDate.of(2026, 3, 1));
            c3.setEndDate(LocalDate.of(2027, 2, 28));
            c3.setNextBillDate(LocalDate.of(2026, 4, 1));
            c3.setCost(95000.0);
            c3.setStatus("Active");
            c3.setDescription("Building security guards deployment contract.");
            facilityContractRepository.save(c3);

            FacilityContract c4 = new FacilityContract();
            c4.setContractId("CON-104");
            c4.setContractName("Fire Safety Systems AMC");
            c4.setVendorName("PowerFix Engineers");
            c4.setCategory("AMC");
            c4.setStartDate(LocalDate.of(2025, 6, 1));
            c4.setEndDate(LocalDate.of(2026, 5, 31));
            c4.setNextBillDate(LocalDate.of(2026, 6, 1));
            c4.setCost(25000.0);
            c4.setStatus("Expired");
            c4.setDescription("Annual maintenance for fire alarm and extinguisher systems.");
            facilityContractRepository.save(c4);

            System.out.println("✅ Seeded Facility Contracts.");
        }

        // Clean up or update any legacy dummy tickets to use real employee records
        java.util.List<ServiceRequest> legacyTickets = serviceRequestRepository.findAll();
        for (ServiceRequest req : legacyTickets) {
            if ("EMP101".equals(req.getEmployeeId())) {
                req.setEmployeeId("EMP114");
                req.setEmployeeName("Om Tripathi");
                serviceRequestRepository.save(req);
            } else if ("EMP102".equals(req.getEmployeeId())) {
                req.setEmployeeId("EMP601");
                req.setEmployeeName("Neha Verma");
                serviceRequestRepository.save(req);
            } else if ("EMP103".equals(req.getEmployeeId())) {
                req.setEmployeeId("EMP129");
                req.setEmployeeName("Saumya Katare");
                serviceRequestRepository.save(req);
            } else if ("EMP104".equals(req.getEmployeeId())) {
                req.setEmployeeId("EMP187");
                req.setEmployeeName("Om Singrore");
                serviceRequestRepository.save(req);
            } else if ("EMP105".equals(req.getEmployeeId())) {
                req.setEmployeeId("EMP201");
                req.setEmployeeName("Priya Sharma");
                serviceRequestRepository.save(req);
            } else if ("EMP106".equals(req.getEmployeeId())) {
                req.setEmployeeId("EMP301");
                req.setEmployeeName("Kavita Finance");
                serviceRequestRepository.save(req);
            } else if ("EMP107".equals(req.getEmployeeId())) {
                req.setEmployeeId("EMP401");
                req.setEmployeeName("Sneha Gupta");
                serviceRequestRepository.save(req);
            } else if ("EMP108".equals(req.getEmployeeId())) {
                req.setEmployeeId("EMP501");
                req.setEmployeeName("Ravi IT");
                serviceRequestRepository.save(req);
            } else if ("EMP109".equals(req.getEmployeeId())) {
                req.setEmployeeId("EMP1101");
                req.setEmployeeName("Pooja Singh");
                serviceRequestRepository.save(req);
            } else if ("EMP110".equals(req.getEmployeeId())) {
                req.setEmployeeId("EMP701");
                req.setEmployeeName("Amit Project");
                serviceRequestRepository.save(req);
            } else if ("EMP111".equals(req.getEmployeeId())) {
                req.setEmployeeId("EMP114");
                req.setEmployeeName("Om Tripathi");
                serviceRequestRepository.save(req);
            } else if ("EMP112".equals(req.getEmployeeId())) {
                req.setEmployeeId("EMP601");
                req.setEmployeeName("Neha Verma");
                serviceRequestRepository.save(req);
            } else if ("EMP113".equals(req.getEmployeeId())) {
                req.setEmployeeId("EMP129");
                req.setEmployeeName("Saumya Katare");
                serviceRequestRepository.save(req);
            }
        }

        // Seed Facility Tickets
        long facTicketCount = serviceRequestRepository.findAll().stream()
                .filter(t -> "FACILITIES".equalsIgnoreCase(t.getType()) || "FACILITY".equalsIgnoreCase(t.getType()))
                .count();
        if (facTicketCount == 0) {
            seedFacilityTicket("TKT-1024", "EMP114", "Om Tripathi", "Office Maintenance", "Printer Toner Replacement", "Medium", "Open", LocalDate.now(), "The printer in Block A level 3 is out of black toner.");
            seedFacilityTicket("TKT-1025", "EMP601", "Neha Verma", "Workstation Layout", "Desk Repair - Block B", "Low", "Assigned", LocalDate.now().minusDays(1), "Desk drawer lock is jammed.");
            seedFacilityTicket("TKT-1026", "EMP129", "Saumya Katare", "Power & Lighting", "AC Adjustment in Conference Room", "High", "In Progress", LocalDate.now().minusDays(2), "AC remote not working, temperature locked at 16 degrees.");
            seedFacilityTicket("TKT-1027", "EMP187", "Om Singrore", "Access Control", "Physical Key Request", "Medium", "Closed", LocalDate.now().minusDays(5), "Requesting drawer key for new locker.");
            seedFacilityTicket("TKT-1028", "EMP201", "Priya Sharma", "Space Allocation", "Permanent Cabin Allocation", "High", "Open", LocalDate.now().minusDays(3), "Relocated to Delhi branch, need permanent desk assignment.");
            seedFacilityTicket("TKT-1029", "EMP301", "Kavita Finance", "Passes & Access", "Vehicle Parking Sticker", "Low", "Closed", LocalDate.now().minusDays(10), "New car registration sticker.");
            seedFacilityTicket("TKT-1030", "EMP401", "Sneha Gupta", "Furniture Request", "Ergonomic Standing Desk", "Medium", "Blocked", LocalDate.now().minusDays(4), "Medical recommendation for standing desk.");
            seedFacilityTicket("TKT-1031", "EMP501", "Ravi IT", "Event Setup", "Conference Room AV Setup", "High", "Closed", LocalDate.now().minusDays(8), "Annual board meet AV requirements setup.");
            seedFacilityTicket("TKT-1032", "EMP1101", "Pooja Singh", "Office Maintenance", "Leaking Tap in Cafeteria", "Medium", "Open", LocalDate.now().minusDays(1), "Water leakage near washbasin.");
            seedFacilityTicket("TKT-1033", "EMP701", "Amit Project", "Power & Lighting", "Replacing fused tubelights", "Low", "Open", LocalDate.now(), "Two tube lights fused in cabin 4.");
            seedFacilityTicket("TKT-1034", "EMP114", "Om Tripathi", "Space Allocation", "Extra seating for interns", "Medium", "Open", LocalDate.now().minusDays(2), "Need 3 temporary chairs in bays 12-14.");
            seedFacilityTicket("TKT-1035", "EMP601", "Neha Verma", "Furniture Request", "Drawer repair", "Low", "Closed", LocalDate.now().minusDays(12), "Drawer handle broken.");
            seedFacilityTicket("TKT-1036", "EMP129", "Saumya Katare", "Access Control", "Temporary visitor pass", "High", "Closed", LocalDate.now().minusDays(6), "Visitor pass for client delegation.");
            System.out.println("✅ Seeded Facilities Service Requests.");
        }

        // Seed IT Data
        if (itAssetRepository.count() == 0) {
            seedItAsset("IT-AST-001", "Laptop", "Dell", "Latitude 5420", "DL12345", "EMP0001", "Active", LocalDate.now().minusYears(1), LocalDate.now().plusYears(2), "Delhi Office");
            seedItAsset("IT-AST-002", "Monitor", "HP", "EliteDisplay E243", "HP8877", "EMP201", "Active", LocalDate.now().minusYears(2), LocalDate.now().plusYears(1), "Mumbai Office");
            seedItAsset("IT-AST-003", "Keyboard", "Logitech", "MX Keys", "LT9988", "EMP0001", "Active", LocalDate.now().minusMonths(6), LocalDate.now().plusYears(2), "Delhi Office");
            seedItAsset("IT-AST-004", "Laptop", "Apple", "MacBook Pro 16", "MB5544", "EMP114", "Maintenance", LocalDate.now().minusYears(1), LocalDate.now().plusYears(1), "Home Office");
            seedItAsset("IT-AST-005", "Laptop", "Lenovo", "ThinkPad T14", "LN3322", "EMP187", "Active", LocalDate.now().minusMonths(8), LocalDate.now().plusYears(2), "Delhi Office");
            seedItAsset("IT-AST-006", "Server", "Dell", "PowerEdge R740", "SRV1122", "IT-ROOM", "Active", LocalDate.now().minusYears(3), LocalDate.now().plusYears(2), "Server Room A");
            seedItAsset("IT-AST-007", "Printer", "HP", "LaserJet Pro", "PR7766", "FLOOR-2", "Active", LocalDate.now().minusYears(2), LocalDate.now().plusYears(1), "Floor 2 Bay B");
            seedItAsset("IT-AST-008", "Laptop", "HP", "ProBook 450", "HP4455", "UNASSIGNED", "Active", LocalDate.now().minusMonths(3), LocalDate.now().plusYears(2), "IT Storage");
            seedItAsset("IT-AST-009", "Monitor", "Dell", "UltraSharp U2720Q", "DL5566", "EMP301", "Active", LocalDate.now().minusYears(1), LocalDate.now().plusYears(2), "Delhi Office");
            seedItAsset("IT-AST-010", "Laptop", "Lenovo", "ThinkPad X1", "LN4488", "EMP129", "Disposed", LocalDate.now().minusYears(4), LocalDate.now().minusYears(1), "Scrap Storage");
            System.out.println("✅ Seeded IT Assets.");
        }

        if (securityPolicyRepository.count() == 0) {
            seedSecurityPolicy("Firewall Policy", "Firewall", "Configures inbound and outbound traffic rules for WhiteCircle corporate network.", "Active", LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(5), 98);
            seedSecurityPolicy("Data Encryption Policy", "Encryption", "Mandates AES-256 encryption for all data-at-rest on company laptops and databases.", "Active", LocalDate.now().minusWeeks(2), LocalDate.now().plusMonths(6), 100);
            seedSecurityPolicy("Multi-Factor Authentication", "Access Control", "Requires MFA for accessing VPN, emails, and internal HRMS portal from outside office.", "Active", LocalDate.now().minusMonths(3), LocalDate.now().plusMonths(3), 95);
            seedSecurityPolicy("BYOD Security Policy", "Access Control", "Defines MDM security profiles required for personal mobile devices used for work.", "Under Review", LocalDate.now().minusMonths(5), LocalDate.now().plusWeeks(2), 75);
            seedSecurityPolicy("Wireless Network Security", "Network Security", "Standardizes WPA3 security and guest network isolation across branches.", "Non-Compliant", LocalDate.now().minusMonths(6), LocalDate.now().minusDays(2), 45);
            System.out.println("✅ Seeded Security Policies.");
        }

        if (backupScheduleRepository.count() == 0) {
            seedBackupSchedule("Daily Incremental Backup", "Incremental", "Daily", "/data/users", "/backup/daily", "Completed", LocalDate.now().minusDays(1), LocalDate.now(), 12.5);
            seedBackupSchedule("Weekly Full Backup", "Full", "Weekly", "/data/all", "/backup/weekly", "Completed", LocalDate.now().minusDays(3), LocalDate.now().plusDays(4), 150.0);
            seedBackupSchedule("Database Dump Backup", "Full", "Daily", "/db/prod", "/backup/db", "Completed", LocalDate.now().minusDays(1), LocalDate.now(), 45.2);
            seedBackupSchedule("System Image Backup", "Full", "Monthly", "/system/OS", "/backup/image", "Scheduled", LocalDate.now().minusDays(15), LocalDate.now().plusDays(15), 80.0);
            seedBackupSchedule("Configuration Files Backup", "Differential", "Weekly", "/config", "/backup/config", "Failed", LocalDate.now().minusDays(5), LocalDate.now().plusDays(2), 1.2);
            System.out.println("✅ Seeded Backup Schedules.");
        }

        if (itBroadcastRepository.count() == 0) {
            seedItBroadcast("Scheduled Network Maintenance", "We will be performing scheduled maintenance on the primary switch this Saturday from 10 PM to 2 AM. Expect brief network interruptions.", "High", "Maintenance", "Active", "EMP0001", LocalDate.now(), LocalDate.now().plusDays(5));
            seedItBroadcast("Update Critical Security Patch", "A critical security update for Windows OS has been released. Please restart your systems before leaving today to apply the updates.", "High", "Security", "Active", "EMP0001", LocalDate.now().minusDays(1), LocalDate.now().plusDays(3));
            seedItBroadcast("Server Migration Complete", "The migration of the staging environment to the new cloud server is complete. Please update your host files accordingly.", "Normal", "General", "Active", "EMP0001", LocalDate.now().minusDays(2), LocalDate.now().plusDays(10));
            System.out.println("✅ Seeded IT Broadcasts.");
        }

        long itTicketCount = serviceRequestRepository.findAll().stream()
                .filter(t -> "IT".equalsIgnoreCase(t.getType()))
                .count();
        if (itTicketCount == 0) {
            seedItTicket("IT-TKT-101", "EMP114", "Om Tripathi", "Software Issue", "IntelliJ License Expired", "High", "Open", LocalDate.now(), "IntelliJ IDEA Ultimate license has expired. Need renewal key.", "EMP0001");
            seedItTicket("IT-TKT-102", "EMP601", "Neha Verma", "Hardware Issue", "Laptop Battery Replacement", "Medium", "In Progress", LocalDate.now().minusDays(1), "Laptop battery draining in less than 30 minutes. Requesting replacement.", "EMP0001");
            seedItTicket("IT-TKT-103", "EMP129", "Saumya Katare", "Access/Permission", "GitHub Repo Access Request", "High", "Open", LocalDate.now(), "Need read/write access to HRMS project repository on GitHub.", "EMP0001");
            seedItTicket("IT-TKT-104", "EMP187", "Om Singrore", "Network/Connectivity", "VPN Disconnects Frequently", "Medium", "Closed", LocalDate.now().minusDays(4), "GlobalProtect VPN disconnects every 10 minutes when working from home.", "EMP0001");
            seedItTicket("IT-TKT-105", "EMP201", "Priya Sharma", "Software Issue", "MS Office Activation", "Low", "Closed", LocalDate.now().minusDays(5), "Microsoft Excel showing 'Activation Required' error.", "EMP0001");
            seedItTicket("IT-TKT-106", "EMP301", "Kavita Finance", "Access/Permission", "Database Access - Prod ReadOnly", "High", "Closed", LocalDate.now().minusDays(8), "Need readonly credentials to production database for audit verification.", "EMP0001");
            seedItTicket("IT-TKT-107", "EMP401", "Sneha Gupta", "Hardware Issue", "Dual Monitor Setup Request", "Medium", "Open", LocalDate.now().minusDays(2), "Requesting an extra 24-inch monitor for recruiter dashboard operations.", "EMP0001");
            seedItTicket("IT-TKT-108", "EMP501", "Ravi IT", "Network/Connectivity", "Wi-Fi Connectivity in Cafeteria", "Low", "Closed", LocalDate.now().minusDays(12), "No signal or slow speed in the cafeteria area.", "EMP0001");
            seedItTicket("IT-TKT-109", "EMP1101", "Pooja Singh", "Software Issue", "Docker Desktop Startup Fail", "High", "Open", LocalDate.now().minusDays(1), "Docker Desktop failing to start after recent Windows update.", "EMP0001");
            seedItTicket("IT-TKT-110", "EMP701", "Amit Project", "Access/Permission", "Jira Project Board Access", "Medium", "Open", LocalDate.now(), "Need developer access to WhiteCircle HRMS project board.", "EMP0001");
            System.out.println("✅ Seeded IT Service Requests.");
        }

        seedLndData();
    }


    private Role getOrCreateRole(String roleName) {
        return roleRepository.findByRoleName(roleName).orElseGet(() -> {
            Role newRole = new Role();
            newRole.setRoleName(roleName);
            return roleRepository.save(newRole);
        });
    }

    private Permission getOrCreatePermission(String permissionName) {
        return permissionRepository.findByPermissionName(permissionName).orElseGet(() -> {
            Permission newPermission = new Permission();
            newPermission.setPermissionName(permissionName);
            return permissionRepository.save(newPermission);
        });
    }


    private void seedLeaveRequest(String username, String type, LocalDate from, LocalDate to, Double days, String reason, String status) {
        User u = userRepository.findByUsername(username).orElse(null);
        if (u != null) {
            LeaveRequest lr = new LeaveRequest();
            lr.setUser(u);
            lr.setLeaveType(type);
            lr.setFromDate(from);
            lr.setToDate(to);
            lr.setTotalDays(days);
            lr.setReason(reason);
            lr.setStatus(status);
            lr.setCreatedAt(LocalDate.now());
            leaveRequestRepository.save(lr);
        }
    }


    private void updateSeededEmployee(String username, String department, String designation) {
        User u = userRepository.findByUsername(username).orElse(null);
        if (u != null) {
            EmployeeProfile p = u.getEmployeeProfile();
            if (p == null) {
                p = new EmployeeProfile();
                p.setUser(u);
            }
            p.setDepartment(department);
            p.setEmployeeCode(username);
            if (designation != null) {
                p.setDesignation(designation);
            }
            u.setEmployeeProfile(p);
            userRepository.save(u);
            System.out.println("✏️ Updated existing user fields for " + username);
        }
     }

    private void seedFacilityTicket(String ticketId, String employeeId, String employeeName, String category, String detailItem, String priority, String status, LocalDate submissionDate, String justification) {
        ServiceRequest req = new ServiceRequest();
        req.setTicketId(ticketId);
        req.setEmployeeId(employeeId);
        req.setEmployeeName(employeeName);
        req.setType("FACILITIES");
        req.setCategory(category);
        req.setDetailItem(detailItem);
        req.setPriority(priority);
        req.setStatus(status);
        req.setSubmissionDate(submissionDate);
        req.setJustification(justification);
        req.setDepartment("Facilities");
        serviceRequestRepository.save(req);
    }

    private void seedRewardsTicket(String ticketId, String employeeId, String employeeName, String category, String detailItem, int points, String status, String justification, String location) {
        ServiceRequest req = new ServiceRequest();
        req.setTicketId(ticketId);
        req.setEmployeeId(employeeId);
        req.setEmployeeName(employeeName);
        req.setType("REWARDS");
        req.setCategory(category);
        req.setDetailItem(detailItem);
        req.setPriority("Medium");
        req.setStatus(status);
        req.setSubmissionDate(LocalDate.now().minusDays(3));
        req.setJustification(justification);
        req.setDepartment("Rewards");
        req.setLocation(location);
        req.setDurationOrLevel(String.valueOf(points));
        serviceRequestRepository.save(req);
    }

    private void seedItAsset(String assetTag, String assetType, String brand, String model, String serialNumber, String assignedTo, String status, LocalDate purchaseDate, LocalDate warrantyExpiry, String location) {
        ItAsset asset = new ItAsset();
        asset.setAssetTag(assetTag);
        asset.setAssetType(assetType);
        asset.setBrand(brand);
        asset.setModel(model);
        asset.setSerialNumber(serialNumber);
        asset.setAssignedTo(assignedTo);
        asset.setStatus(status);
        asset.setPurchaseDate(purchaseDate);
        asset.setWarrantyExpiry(warrantyExpiry);
        asset.setLocation(location);
        itAssetRepository.save(asset);
    }

    private void seedSecurityPolicy(String policyName, String category, String description, String status, LocalDate lastAuditDate, LocalDate nextReviewDate, Integer complianceScore) {
        SecurityPolicy policy = new SecurityPolicy();
        policy.setPolicyName(policyName);
        policy.setCategory(category);
        policy.setDescription(description);
        policy.setStatus(status);
        policy.setLastAuditDate(lastAuditDate);
        policy.setNextReviewDate(nextReviewDate);
        policy.setComplianceScore(complianceScore);
        securityPolicyRepository.save(policy);
    }

    private void seedBackupSchedule(String backupName, String backupType, String frequency, String source, String destination, String status, LocalDate lastRunDate, LocalDate nextRunDate, Double sizeGB) {
        BackupSchedule backup = new BackupSchedule();
        backup.setBackupName(backupName);
        backup.setBackupType(backupType);
        backup.setFrequency(frequency);
        backup.setSource(source);
        backup.setDestination(destination);
        backup.setStatus(status);
        backup.setLastRunDate(lastRunDate);
        backup.setNextRunDate(nextRunDate);
        backup.setSizeGB(sizeGB);
        backupScheduleRepository.save(backup);
    }

    private void seedItBroadcast(String title, String message, String priority, String category, String status, String createdBy, LocalDate createdDate, LocalDate expiryDate) {
        ItBroadcast broadcast = new ItBroadcast();
        broadcast.setTitle(title);
        broadcast.setMessage(message);
        broadcast.setPriority(priority);
        broadcast.setCategory(category);
        broadcast.setStatus(status);
        broadcast.setCreatedBy(createdBy);
        broadcast.setCreatedDate(createdDate);
        broadcast.setExpiryDate(expiryDate);
        itBroadcastRepository.save(broadcast);
    }

    private void seedItTicket(String ticketId, String employeeId, String employeeName, String category, String detailItem, String priority, String status, LocalDate submissionDate, String justification, String assignedTo) {
        ServiceRequest req = new ServiceRequest();
        req.setTicketId(ticketId);
        req.setEmployeeId(employeeId);
        req.setEmployeeName(employeeName);
        req.setType("IT");
        req.setCategory(category);
        req.setDetailItem(detailItem);
        req.setPriority(priority);
        req.setStatus(status);
        req.setSubmissionDate(submissionDate);
        req.setJustification(justification);
        req.setDepartment("IT");
        req.setAssignedTo(assignedTo);
        serviceRequestRepository.save(req);
    }

    private void seedLndData() {
        if (learningStrategyRepository.count() == 0) {
            learningStrategyRepository.save(new com.example.admindashboard.model.LearningStrategy("Future Ready Workforce", "Build future skills across the organization", "Improve capability for digital transformation", "All Employees", "Active", "08 July 2026"));
            learningStrategyRepository.save(new com.example.admindashboard.model.LearningStrategy("Leadership Excellence", "Develop leadership and management capabilities", "Strengthen leadership bench strength", "Managers & Above", "Active", "08 July 2026"));
            learningStrategyRepository.save(new com.example.admindashboard.model.LearningStrategy("Technical Upskilling", "Enhance domain and technical competencies", "Improve product & engineering performance", "Engineering Teams", "Active", "08 July 2026"));
            learningStrategyRepository.save(new com.example.admindashboard.model.LearningStrategy("Customer Centricity", "Improve customer focus and experience", "Increase customer satisfaction", "Sales & Support Teams", "Draft", "08 July 2026"));
        }

        if (learningProgramRepository.count() == 0) {
            // Technical (10)
            learningProgramRepository.save(new com.example.admindashboard.model.LearningProgram("Full Stack Developer Mastery", "Technical", "Certification", "Comprehensive full-stack development skills", "Engineering", "Software Engineers", "Build digital capabilities", "Rahul Mehta", "6 Months", "Online", "2026-01-15", "2026-07-15", "Intermediate", "Java, React, Spring", "Master Frontend and Backend Architecture", "Active"));
            learningProgramRepository.save(new com.example.admindashboard.model.LearningProgram("Cloud Architecture & DevOps", "Technical", "Workshop", "Modern cloud and container orchestration", "Engineering", "DevOps & Cloud Engineers", "Accelerate cloud transition", "Arjun Kapoor", "3 Months", "Blended", "2026-02-01", "2026-05-01", "Advanced", "AWS, Kubernetes, Docker", "Implement CI/CD pipelines", "Active"));
            for (int i = 3; i <= 10; i++) {
                learningProgramRepository.save(new com.example.admindashboard.model.LearningProgram("Technical Specialization " + i, "Technical", "Training", "Domain specific technical skills", "Engineering", "Engineering Teams", "Improve performance", "Rahul Mehta", "2 Months", "Online", "2026-03-01", "2026-05-01", "Intermediate", "Tech", "Technical proficiency", "Active"));
            }
            // Behavioral (6)
            for (int i = 1; i <= 6; i++) {
                learningProgramRepository.save(new com.example.admindashboard.model.LearningProgram("Behavioral & Soft Skills " + i, "Behavioral", "Workshop", "Workplace communication and teamwork", "All Departments", "All Employees", "Enhance collaboration", "Sneha Patil", "1 Month", "Offline", "2026-04-01", "2026-05-01", "Basic", "Soft Skills", "Effective communication", "Active"));
            }
            // Leadership (4)
            for (int i = 1; i <= 4; i++) {
                learningProgramRepository.save(new com.example.admindashboard.model.LearningProgram("Leadership Track " + i, "Leadership", "Executive", "Management and strategic thinking", "Leadership", "Managers & Above", "Bench strength", "Vivek Nair", "4 Months", "Blended", "2026-01-10", "2026-05-10", "Advanced", "Leadership", "Executive leadership", "Active"));
            }
            // Compliance (2)
            for (int i = 1; i <= 2; i++) {
                learningProgramRepository.save(new com.example.admindashboard.model.LearningProgram("Mandatory Compliance " + i, "Compliance", "Compliance", "Regulatory & POSH ethics training", "HR & Legal", "All Employees", "Maintain compliance", "Neha Verma", "2 Weeks", "Online", "2026-05-01", "2026-05-15", "Basic", "Compliance", "100% compliance adherence", "Active"));
            }
            // Others (2)
            for (int i = 1; i <= 2; i++) {
                learningProgramRepository.save(new com.example.admindashboard.model.LearningProgram("General Skills " + i, "Others", "General", "Cross-functional productivity skills", "Operations", "All Staff", "Operational excellence", "Rahul Mehta", "1 Month", "Online", "2026-06-01", "2026-07-01", "Basic", "General", "Productivity enhancement", "Active"));
            }
        }

        if (assessmentRepository.count() == 0) {
            assessmentRepository.save(new com.example.admindashboard.model.Assessment("ASMT-001", "Leadership Skills Assessment", "Assessment", "Rahul Mehta", "Leadership", "70% or above", 70, "Active", 245, 82.0, "Managers & Above", "Intermediate", 45, 30, 2, 60, false, true, "Online", "28 May 2026"));
            assessmentRepository.save(new com.example.admindashboard.model.Assessment("QUIZ-012", "Sales Knowledge Quiz", "Quiz", "Sneha Patil", "Sales & Marketing", "60% or above", 60, "Active", 312, 74.0, "Sales Executives", "Basic", 20, 15, 2, 30, false, true, "Online", "28 May 2026"));
            assessmentRepository.save(new com.example.admindashboard.model.Assessment("ASMT-018", "Excel Advanced Test", "Assessment", "Arjun Kapoor", "Finance", "65% or above", 65, "Active", 189, 68.0, "Finance & Accounts", "Advanced", 60, 25, 4, 100, true, true, "Online", "28 May 2026"));
            assessmentRepository.save(new com.example.admindashboard.model.Assessment("QUIZ-021", "Compliance Awareness Quiz", "Quiz", "Neha Verma", "HR", "80% or above", 80, "Draft", 0, 0.0, "All Employees", "Basic", 15, 10, 2, 20, false, false, "Online", "28 May 2026"));
            assessmentRepository.save(new com.example.admindashboard.model.Assessment("ASMT-027", "Customer Service Assessment", "Assessment", "Vivek Nair", "Customer Support", "70% or above", 70, "Closed", 156, 71.0, "Support Team", "Intermediate", 30, 20, 5, 100, false, true, "Online", "28 May 2026"));

            // Add remaining active/draft/closed/expired assessments to reach total 56
            for (int i = 6; i <= 32; i++) {
                assessmentRepository.save(new com.example.admindashboard.model.Assessment("ASMT-0" + i, "Technical Competency Assessment " + i, "Assessment", "Rahul Mehta", "Engineering", "70% or above", 70, "Active", 120 + i, 75.0, "Engineering Teams", "Intermediate", 45, 30, 2, 60, false, true, "Online", "28 May 2026"));
            }
            for (int i = 33; i <= 44; i++) {
                assessmentRepository.save(new com.example.admindashboard.model.Assessment("QUIZ-0" + i, "Domain Knowledge Quiz " + i, "Quiz", "Sneha Patil", "Product", "60% or above", 60, "Draft", 0, 0.0, "Product Managers", "Basic", 15, 10, 2, 20, false, false, "Online", "28 May 2026"));
            }
            for (int i = 45; i <= 52; i++) {
                assessmentRepository.save(new com.example.admindashboard.model.Assessment("ASMT-0" + i, "Legacy Skill Evaluation " + i, "Assessment", "Arjun Kapoor", "Operations", "65% or above", 65, "Closed", 85, 62.0, "Operations Staff", "Intermediate", 30, 20, 5, 100, false, true, "Online", "28 May 2026"));
            }
            for (int i = 53; i <= 56; i++) {
                assessmentRepository.save(new com.example.admindashboard.model.Assessment("ASMT-0" + i, "Archived Certification Exam " + i, "Assessment", "Neha Verma", "HR", "75% or above", 75, "Expired", 45, 55.0, "HR Team", "Advanced", 60, 40, 2, 100, true, true, "Online", "28 May 2026"));
            }
        }

        if (certificationRepository.count() == 0) {
            certificationRepository.save(new com.example.admindashboard.model.Certification("CERT-001", "Certified Agile Practitioner", "L&D Academy", "Engineering", "2 Years", 142, "Active"));
            certificationRepository.save(new com.example.admindashboard.model.Certification("CERT-002", "Leadership Excellence Certificate", "Executive Board", "Management", "Lifetime", 86, "Active"));
            certificationRepository.save(new com.example.admindashboard.model.Certification("CERT-003", "Data Privacy & GDPR Specialist", "Compliance Office", "Legal & Compliance", "1 Year", 210, "Active"));
            for (int i = 4; i <= 34; i++) {
                certificationRepository.save(new com.example.admindashboard.model.Certification("CERT-0" + i, "Professional Competency Cert " + i, "WhiteCircle L&D", "General", "2 Years", 50 + i, "Active"));
            }
        }
    }
}