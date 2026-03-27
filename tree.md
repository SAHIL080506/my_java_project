# File Tree: hrm_output

**Generated:** 3/27/2026, 7:09:06 AM
**Root Path:** `c:\Users\LENOVO\Documents\hrm_output`

```
├── 📁 sql
│   └── 📄 migrations.sql
├── 📁 src
│   ├── 📁 main
│   │   ├── 📁 java
│   │   │   └── 📁 com
│   │   │       └── 📁 example
│   │   │           └── 📁 hrm_payroll
│   │   │               ├── 📁 Config
│   │   │               │   ├── ☕ AuthInterceptor.java
│   │   │               │   ├── ☕ Config.java
│   │   │               │   └── ☕ GlobalExceptionHandler.java
│   │   │               ├── 📁 Controllers
│   │   │               │   ├── ☕ ActivityController.java
│   │   │               │   ├── ☕ AttendanceController.java
│   │   │               │   ├── ☕ AuthController.java
│   │   │               │   ├── ☕ EmployeeDashboardController.java
│   │   │               │   ├── ☕ FirstController.java
│   │   │               │   ├── ☕ HRController.java
│   │   │               │   ├── ☕ LeaveController.java
│   │   │               │   ├── ☕ PayrollController.java
│   │   │               │   ├── ☕ ProfileController.java
│   │   │               │   ├── ☕ RootController.java
│   │   │               │   └── ☕ TaskController.java
│   │   │               ├── 📁 DTO
│   │   │               │   ├── ☕ ActivityLogResponse.java
│   │   │               │   ├── ☕ AddEmployeeRequest.java
│   │   │               │   ├── ☕ AttendanceResponse.java
│   │   │               │   ├── ☕ DashboardStatsResponse.java
│   │   │               │   ├── ☕ EmployeeDashboardResponse.java
│   │   │               │   ├── ☕ EmployeeFullProfileResponse.java
│   │   │               │   ├── ☕ EmployeeResponse.java
│   │   │               │   ├── ☕ EmployeeTaskResponse.java
│   │   │               │   ├── ☕ ErrorResponse.java
│   │   │               │   ├── ☕ GeneratePayrollRequest.java
│   │   │               │   ├── ☕ LeaveRequestDTO.java
│   │   │               │   ├── ☕ LoginRequest.java
│   │   │               │   ├── ☕ NewEmployeeResponse.java
│   │   │               │   ├── ☕ OnboardingTaskResponse.java
│   │   │               │   ├── ☕ PayrollResponse.java
│   │   │               │   ├── ☕ ProfileResponse.java
│   │   │               │   ├── ☕ ProfileUpdateRequest.java
│   │   │               │   ├── ☕ RegisterRequest.java
│   │   │               │   ├── ☕ TodayAttendanceStats.java
│   │   │               │   └── ☕ UserResponse.java
│   │   │               ├── 📁 Entity
│   │   │               │   ├── ☕ ActivityLog.java
│   │   │               │   ├── ☕ ActivityLogRole.java
│   │   │               │   ├── ☕ Attendance.java
│   │   │               │   ├── ☕ AttendanceStatus.java
│   │   │               │   ├── ☕ Country.java
│   │   │               │   ├── ☕ Employee.java
│   │   │               │   ├── ☕ EmployeeTask.java
│   │   │               │   ├── ☕ Gender.java
│   │   │               │   ├── ☕ LeaveRequest.java
│   │   │               │   ├── ☕ LeaveStatus.java
│   │   │               │   ├── ☕ LeaveType.java
│   │   │               │   ├── ☕ OnboardingTask.java
│   │   │               │   ├── ☕ Payroll.java
│   │   │               │   ├── ☕ RoleType.java
│   │   │               │   ├── ☕ UserStatus.java
│   │   │               │   └── ☕ Users.java
│   │   │               ├── 📁 Repository
│   │   │               │   ├── ☕ ActivityLogRepository.java
│   │   │               │   ├── ☕ AttendanceRepository.java
│   │   │               │   ├── ☕ CountryRepositry.java
│   │   │               │   ├── ☕ EmployeeRepository.java
│   │   │               │   ├── ☕ EmployeeTaskRepository.java
│   │   │               │   ├── ☕ LeaveRequestRepository.java
│   │   │               │   ├── ☕ OnboardingTaskRepository.java
│   │   │               │   ├── ☕ PayrollRepository.java
│   │   │               │   └── ☕ UserRepository.java
│   │   │               ├── 📁 Services
│   │   │               │   ├── ☕ ActivityLogService.java
│   │   │               │   ├── ☕ AttendanceService.java
│   │   │               │   ├── ☕ CountryService.java
│   │   │               │   ├── ☕ EmployeeDashboardService.java
│   │   │               │   ├── ☕ EmployeeService.java
│   │   │               │   ├── ☕ HREmployeeViewService.java
│   │   │               │   ├── ☕ LeaveService.java
│   │   │               │   ├── ☕ PayrollService.java
│   │   │               │   ├── ☕ ProfileService.java
│   │   │               │   ├── ☕ TaskService.java
│   │   │               │   └── ☕ UserService.java
│   │   │               └── ☕ HrmPayrollApplication.java
│   │   └── 📁 resources
│   │       ├── 📁 static
│   │       │   ├── 📁 css
│   │       │   │   └── 🎨 style.css
│   │       │   ├── 📁 js
│   │       │   │   └── 📄 auth.js
│   │       │   ├── 🌐 add-employee.html
│   │       │   ├── 🌐 dashboard.html
│   │       │   ├── 🌐 employee-dashboard.html
│   │       │   ├── 🌐 employee-details.html
│   │       │   ├── 🌐 employees.html
│   │       │   ├── 🌐 hr-dashboard.html
│   │       │   ├── 🌐 index.html
│   │       │   ├── 🌐 manage-leaves.html
│   │       │   ├── 🌐 new-employees.html
│   │       │   ├── 🌐 profile.html
│   │       │   └── 🌐 signup.html
│   │       └── 📄 application.properties
│   └── 📁 test
│       └── 📁 java
│           └── 📁 com
│               └── 📁 example
│                   └── 📁 hrm_payroll
│                       └── ☕ HrmPayrollApplicationTests.java
├── 📁 uploads
│   ├── 🖼️ proof_6_6bb0380c-e793-4208-ba6b-1c996b7c0c20.png
│   ├── 🖼️ proof_7_a2693e0b-81d9-4b48-add2-91605470ce16.png
│   ├── 🖼️ proof_8_99bd4ea5-04b9-4ac2-af51-cda11e4ba7d9.jpg
│   ├── 🖼️ proof_9_d64d9de8-ade7-4c66-bd83-3c7963840240.png
│   └── 📕 resume_26_b8a7faa8-a163-45d4-bc29-760d9731d39a.pdf
└── ⚙️ pom.xml
```

---
*Generated by FileTree Pro Extension*