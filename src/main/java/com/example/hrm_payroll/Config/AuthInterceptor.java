package com.example.hrm_payroll.Config;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.hrm_payroll.Entity.RoleType;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;

        HttpSession session = request.getSession(false);
        String uri = request.getRequestURI();

        // Must be authenticated for all /api/** routes
        if (session == null || session.getAttribute("userId") == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"message\":\"Unauthorized\",\"timestamp\":\"" + java.time.LocalDateTime.now() + "\"}");
            return false;
        }

        RoleType role = (RoleType) session.getAttribute("role");

        // /api/hr/** — HR only
        if (uri.startsWith("/api/hr")) {
            if (role != RoleType.HR) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.setContentType("application/json");
                response.getWriter().write(
                        "{\"message\":\"Access denied: HR role required\",\"timestamp\":\"" +
                        java.time.LocalDateTime.now() + "\"}");
                return false;
            }
        }

        // /api/employee/dashboard — EMPLOYEE only (other /api/employee/** endpoints handle internally)
        if (uri.equals("/api/employee/dashboard") && role != RoleType.EMPLOYEE) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"message\":\"Access denied: Employee role required\",\"timestamp\":\"" +
                    java.time.LocalDateTime.now() + "\"}");
            return false;
        }

        // /api/leaves/**, /api/attendance/**, /api/tasks/**, /api/activity/** — any authenticated user
        // Role enforcement is done inside each controller method

        return true;
    }
}
