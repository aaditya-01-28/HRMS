package com.example.admindashboard.service;

import com.example.admindashboard.model.*;
import com.example.admindashboard.repository.ClientRepository;
import com.example.admindashboard.repository.PermissionRepository;
import com.example.admindashboard.repository.RoleRepository;
import com.example.admindashboard.repository.UserRepository;
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

        // Test Account 1: HR Admin
        if (userRepository.findByUsername("EMP201").isEmpty()) {
            User hrUser = new User();
            hrUser.setUsername("EMP201");
            hrUser.setPassword("{noop}welcome123");
            hrUser.setRole(hrAdminRole);
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
            System.out.println("✅ Created HR Admin -> ID: EMP201");
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
}