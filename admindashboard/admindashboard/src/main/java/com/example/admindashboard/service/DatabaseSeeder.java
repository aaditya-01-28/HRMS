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

    @Override
    public void run(String... args) throws Exception {
                // Always update existing seeded users to ensure they have correct employeeCode and department
        updateSeededEmployee("EMP114", "Engineering", "Marketing Lead");
        updateSeededEmployee("EMP187", "Product", "Project Manager");
        updateSeededEmployee("EMP129", "Engineering", "IOS Developer");
        updateSeededEmployee("EMP201", "HR", "HR Director");
        updateSeededEmployee("ADMIN001", "IT", "Company Admin / IT Admin");

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
}