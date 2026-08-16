# 🏢 Enterprise HRMS (Human Resource Management System)

> An enterprise-grade, comprehensive Human Resource Management & Operations Platform designed to streamline the entire employee lifecycle—from recruitment and onboarding to attendance, payroll, mediclaim, project workflows, and separation.

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17%2B-orange.svg)](https://www.oracle.com/java/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-Proprietary-red.svg)]()

---

## 📖 About Us & Project Overview

### 🌟 About the Project
**Enterprise HRMS** is an all-in-one digital workplace and operations management solution crafted to unify HR operations, management workflows, and employee self-service into a seamless, high-performance ecosystem. 

Designed with modern enterprise governance standards, role-based access control (RBAC), and automated approval chains, the system eliminates administrative friction, enhances transparency, and delivers real-time analytics across all organizational tiers.

---

## 🎯 Key Objectives & Core Values

- **Unified Employee Experience (ESS)**: Empower employees with a centralized "MySpace" portal for leaves, attendance regularization, payroll slips, peer appreciation, goals, asset tracking, and travel/commute bookings.
- **End-to-End Workforce Management**: Automate full-cycle HR processes including background verification (BGV), dynamic pre-onboarding/onboarding, bulk profile updates, asset provisioning, and offboarding/separation workflows.
- **Robust Role-Based Portals**: Dedicated workspaces and automated approval pipelines for **Super Admin, Senior HR, Senior Managers, Accounts, Facility, Transport, IT Support, Rewards, and External Clients**.
- **Data Integrity & Compliance**: Secure authentication with Spring Security, automated audit logs, encrypted credential management, and role-level authorization.
- **Operational Agility**: Real-time communication via WebSockets, email notifications with SMTP integration, Excel/PDF report generation, and responsive desktop/mobile layouts.

---

## 🏗️ Architecture & Technology Stack

| Layer | Technologies & Tools |
| :--- | :--- |
| **Backend Core** | Java 17, Spring Boot 3.4.1, Spring Data JPA (Hibernate), Spring Security |
| **Frontend UI/UX** | Thymeleaf Templating Engine, HTML5, Vanilla & Custom CSS, Responsive Layouts |
| **Database** | PostgreSQL, Relational DB Schema |
| **Real-time & Communication** | Spring WebSocket (STOMP/SockJS), Spring Mail (SMTP Notification Engine) |
| **Reporting & Export** | Apache POI (Excel XLSX Import/Export), OpenPDF (PDF Payslip & Report Generation) |
| **Build & Tooling** | Apache Maven, Docker, Spring Dotenv |

---

## 🚀 Key Modules & Feature Highlights

### 1. 🛡️ Super Admin Control Center
- **Enterprise Dashboard**: Real-time KPI counters, department distribution, attendance metrics, and quick action widgets.
- **Workforce Management**: Pre-onboarding checklist, employee details management, bulk profile updater via Excel, and force approval controls.
- **Background Verification (BGV)**: Dedicated candidate verification pipeline with document validation and recruiter assignment.
- **Asset & Separation Hub**: Centralized hardware/software asset allocation and exit clearance workflows.
- **System Governance**: Role configuration, permission matrix, client onboarding, and system audit logs.

### 2. 👥 Senior HR & Talent Management
- **Attendance & Regularization**: Shift management, biometric/web clock-in tracking, and regularization approval queue.
- **Leave Management & Wallet**: Leave entitlement allocation, compensatory-off tracking, and multi-tier leave approval engine.
- **Recruitment & Applicant Tracking (ATS)**: Job requisitions, candidate pipeline, interview scheduling, and offer letter dispatch.
- **Learning & Development (LMS)**: Course catalog, training calendar, employee enrollments, and completion certifications.
- **Payroll & Benefits**: Salary structure setup, tax calculations, pay run executions, and downloadable PDF payslips.

### 3. 👔 Senior Manager & Department Workflows
- **Team Workspace**: Live team availability, hierarchical org chart, and direct report overview.
- **Timesheet & Timecards**: Daily, weekly, and monthly timecard approvals with project-level billable tracking.
- **Performance & Appraisals**: Goal setting (OKRs/KPIs), 360-degree feedback reviews, and rating submissions.
- **Expense & Travel Management**: Reimbursement claims, travel allowances, and multi-currency expense approvals.

### 4. 💻 Employee Self-Service (MySpace)
- **Profile & Directory**: Personal information, statutory documents, hierarchy view, and emergency contacts.
- **Leave & Attendance**: One-click leave application, balance wallet, timecard submission, and holiday calendar.
- **Mediclaim & Insurance**: Cashless card view, policy benefits, dependent coverage, hospital locator, and claim tracker.
- **My Rides**: Cab booking for office commute, roster requests, driver tracking, and SOS safety tools.
- **My Thanks & Rewards**: Peer-to-peer appreciation badges, reward points wallet, and recognition feed.
- **Facility & IT Requests**: Conference room booking, seat reservation, IT ticket submission, and asset acknowledgment.

### 5. 🤝 Client & Vendor Portal
- **Client Workspace**: Ongoing project deliverables, work-in-progress (WIP) tracker, task boards, and file vault.
- **Support & Feedback**: Dedicated ticketing system, payment tracking, and direct feedback channel.

---

## 📂 Project Structure

```
HRMS/
├── admindashboard/
│   └── admindashboard/
│       ├── pom.xml                               # Maven build configuration & dependencies
│       ├── Dockerfile                            # Container deployment configuration
│       ├── src/
│       │   ├── main/
│       │   │   ├── java/com/example/             # Java Backend Controllers, Models, Repositories, Services
│       │   │   └── resources/
│       │   │       ├── application.properties    # Application & database configurations
│       │   │       ├── static/                   # CSS stylesheets, JavaScript files, images, icons
│       │   │       └── templates/                # Thymeleaf HTML views & workflow portals
│       │   └── test/                             # Unit and integration test suites
│       └── uploads/                              # Uploaded employee documents, receipts & media
└── README.md
```

---

## ⚡ Getting Started & Local Setup

### Prerequisites
- **Java JDK 17** or higher installed
- **Apache Maven 3.8+**
- **PostgreSQL 14+** running locally or accessible via network
- **Git**

### Installation Steps

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/aaditya-01-28/HRMS.git
   cd HRMS/admindashboard/admindashboard
   ```

2. **Configure Database & Environment:**
   Update your database credentials in `src/main/resources/application.properties` or create a `.env` file:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/hrms_db
   spring.datasource.username=your_postgres_user
   spring.datasource.password=your_postgres_password
   spring.jpa.hibernate.ddl-auto=update
   ```

3. **Build the Application:**
   ```bash
   ./mvnw clean install
   # Or on Windows PowerShell:
   .\mvnw.cmd clean install
   ```

4. **Run the Application:**
   ```bash
   ./mvnw spring-boot:run
   # Or on Windows PowerShell:
   .\mvnw.cmd spring-boot:run
   ```

5. **Access the Application:**
   Open your web browser and navigate to:
   ```
   http://localhost:8080
   ```

---

## 🔒 Security & Best Practices

- **Role-Based Authentication**: Strict endpoint security ensuring users access only their designated portals.
- **Session Protection**: CSRF protection, secure cookie handling, and session timeout mechanisms.
- **Data Validation**: Hibernate Validator on all form inputs and REST payloads.
- **Secure File Storage**: Protected upload directory with MIME-type verification for candidate and employee documents.

---

## 👥 Contributors & Maintainers

- **Aaditya Prabhakar** — *Lead Developer & Maintainer* ([@aaditya-01-28](https://github.com/aaditya-01-28))

---

## 📄 License
This project is proprietary and confidential. All rights reserved.
