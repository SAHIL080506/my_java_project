package com.example.hrm_payroll.Services;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.hrm_payroll.DTO.AddEmployeeRequest;
import com.example.hrm_payroll.DTO.DashboardStatsResponse;
import com.example.hrm_payroll.DTO.EmployeeResponse;
import com.example.hrm_payroll.DTO.NewEmployeeResponse;
import com.example.hrm_payroll.Entity.Employee;
import com.example.hrm_payroll.Entity.LeaveRequest;
import com.example.hrm_payroll.Entity.LeaveStatus;
import com.example.hrm_payroll.Entity.RoleType;
import com.example.hrm_payroll.Entity.UserStatus;
import com.example.hrm_payroll.Entity.Users;
import com.example.hrm_payroll.Repository.EmployeeRepository;
import com.example.hrm_payroll.Repository.LeaveRequestRepository;
import com.example.hrm_payroll.Repository.PayrollRepository;
import com.example.hrm_payroll.Repository.UserRepository;

@Service
public class EmployeeService {

    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private PayrollRepository payrollRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private LeaveRequestRepository leaveRequestRepository;
    @Autowired private ActivityLogService activityLogService;
    @Autowired private AttendanceService attendanceService;
    @Autowired private LeaveService leaveService;
    @Autowired private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public DashboardStatsResponse getDashboardStats() {
        YearMonth currentMonth = YearMonth.now();
        String monthStr = currentMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));

        long totalEmployees    = employeeRepository.count();
        long activeEmployees   = attendanceService.countActiveToday();
        long inactiveEmployees = Math.max(0, totalEmployees - activeEmployees);
        long pendingApprovals  = userRepository.findByStatus(UserStatus.PENDING).size();

        double attendanceRate = calculateMonthlyAttendanceRate(currentMonth, totalEmployees);

        BigDecimal totalPayroll = payrollRepository.getMonthlyPayrollTotal(monthStr);
        if (totalPayroll == null) totalPayroll = BigDecimal.ZERO;

        List<Long> empIdsWithPayroll = payrollRepository.findEmpIdsWithPayrollForMonth(monthStr);
        long payrollPendingCount = Math.max(0, totalEmployees - empIdsWithPayroll.size());

        long presentToday    = attendanceService.countPresentToday();
        long absentToday     = attendanceService.countAbsentToday(totalEmployees);
        long pendingLeaveCount = leaveService.countPendingLeaves();

        DashboardStatsResponse resp = new DashboardStatsResponse();
        resp.setTotalEmployees(totalEmployees);
        resp.setActiveEmployees(activeEmployees);
        resp.setInactiveEmployees(inactiveEmployees);
        resp.setPendingApprovals(pendingApprovals);
        resp.setAttendanceRate(attendanceRate);
        resp.setTotalMonthlyPayroll(totalPayroll);
        resp.setPayrollPendingCount(payrollPendingCount);
        resp.setCurrentMonth(monthStr);
        resp.setPresentToday(presentToday);
        resp.setAbsentToday(absentToday);
        resp.setPendingLeaveCount(pendingLeaveCount);
        return resp;
    }

    private double calculateMonthlyAttendanceRate(YearMonth yearMonth, long totalEmployees) {
        if (totalEmployees == 0) return 100.0;
        LocalDate startOfMonth = yearMonth.atDay(1);
        LocalDate endOfMonth   = yearMonth.atEndOfMonth();
        LocalDate today        = LocalDate.now();
        if (endOfMonth.isAfter(today)) endOfMonth = today;
        long workingDays = countWorkingDays(startOfMonth, endOfMonth);
        if (workingDays == 0) return 100.0;
        List<LeaveRequest> approvedLeaves = leaveRequestRepository
                .findApprovedLeavesInDateRange(LeaveStatus.APPROVED, startOfMonth, endOfMonth);
        long totalLeaveDays = 0;
        for (LeaveRequest leave : approvedLeaves) {
            LocalDate ls = leave.getStartDate().isBefore(startOfMonth) ? startOfMonth : leave.getStartDate();
            LocalDate le = leave.getEndDate().isAfter(endOfMonth) ? endOfMonth : leave.getEndDate();
            totalLeaveDays += countWorkingDays(ls, le);
        }
        long totalPossible = workingDays * totalEmployees;
        long actual        = totalPossible - totalLeaveDays;
        return Math.round((double) actual / totalPossible * 10000.0) / 100.0;
    }

    private long countWorkingDays(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) return 0;
        long days = 0;
        LocalDate cur = start;
        while (!cur.isAfter(end)) {
            DayOfWeek d = cur.getDayOfWeek();
            if (d != DayOfWeek.SATURDAY && d != DayOfWeek.SUNDAY) days++;
            cur = cur.plusDays(1);
        }
        return days;
    }

    /**
     * Returns ONLY employees with user.status = ACTIVE
     */
    public List<EmployeeResponse> searchEmployees(String name, String department, String designation) {
        List<Employee> employees = employeeRepository.searchActiveEmployees(name, department, designation, UserStatus.ACTIVE);
        YearMonth currentMonth = YearMonth.now();
        String monthStr = currentMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        return employees.stream().map(emp -> {
            EmployeeResponse r = EmployeeResponse.fromEntity(emp);
            r.setPayrollGeneratedForCurrentMonth(
                payrollRepository.findByEmployeeAndSalaryMonth(emp, monthStr).isPresent());
            return r;
        }).collect(Collectors.toList());
    }

    public List<EmployeeResponse> getAllEmployees() {
        YearMonth currentMonth = YearMonth.now();
        String monthStr = currentMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        return employeeRepository.findAll().stream().map(emp -> {
            EmployeeResponse r = EmployeeResponse.fromEntity(emp);
            r.setPayrollGeneratedForCurrentMonth(
                payrollRepository.findByEmployeeAndSalaryMonth(emp, monthStr).isPresent());
            return r;
        }).collect(Collectors.toList());
    }

    public Employee getEmployeeById(Long empId) {
        return employeeRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + empId));
    }

    public EmployeeResponse addEmployee(AddEmployeeRequest request) {

        if (request.getFirstName() == null || request.getFirstName().trim().isEmpty())
            throw new IllegalArgumentException("First name is required");

        if (request.getEmail() == null || request.getEmail().trim().isEmpty())
            throw new IllegalArgumentException("Email is required");

        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByUsername(email))
            throw new IllegalArgumentException("User with this email already exists");

        if (userRepository.existsByEmail(email))
            throw new IllegalArgumentException("Email already exists");

        RoleType role = (request.getRole() != null) ? request.getRole() : RoleType.EMPLOYEE;

        String tempPassword = "Welcome@123";

        Users user = new Users();
        user.setUsername(email);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(tempPassword));
        user.setRole(role);
        user.setStatus(UserStatus.ACTIVE);

        Users savedUser = userRepository.save(user);

        Employee employee = new Employee();
        employee.setUser(savedUser);
        employee.setFirstName(request.getFirstName().trim());
        employee.setMiddleName(request.getMiddleName() != null ? request.getMiddleName().trim() : null);
        employee.setLastName(request.getLastName() != null ? request.getLastName().trim() : null);
        employee.setPhone(request.getPhone());
        employee.setDepartment(request.getDepartment());
        employee.setDesignation(request.getDesignation());
        employee.setGender(request.getGender());
        employee.setAddress(request.getAddress());
        employee.setProfileCompleted(false);

        Employee savedEmployee = employeeRepository.save(employee);

        activityLogService.logHR(
            "EMPLOYEE_ADDED",
            "New " + role.name() + " added by HR: "
                + savedEmployee.getFullName()
                + " (" + savedUser.getEmail() + ")"
                + " — temp password: " + tempPassword);

        return EmployeeResponse.fromEntity(savedEmployee);
    }

    public List<NewEmployeeResponse> getNewEmployeesThisMonth() {
        return employeeRepository.findNewEmployeesThisMonth()
                .stream().map(NewEmployeeResponse::fromEntity).collect(Collectors.toList());
    }

    public List<String> getAllDepartments() {
        return employeeRepository.findAllDepartments();
    }

    public List<String> getAllDesignations() {
        return employeeRepository.findAllDesignations();
    }
}
