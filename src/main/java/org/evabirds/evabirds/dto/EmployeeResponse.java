package org.evabirds.evabirds.dto;

import org.evabirds.evabirds.entity.Employee;

import java.time.LocalDateTime;

public class EmployeeResponse {

    private Long id;
    private String employeeId;
    private String fullName;
    private String email;
    private String role;
    private String department;
    private String designation;
    private String phoneNumber;
    private boolean enabled;
    private LocalDateTime createdAt;

    public EmployeeResponse() {
    }

    public static EmployeeResponse fromEntity(Employee emp) {
        if (emp == null) return null;
        EmployeeResponse dto = new EmployeeResponse();
        dto.setId(emp.getId());
        dto.setEmployeeId(emp.getEmployeeId());
        dto.setFullName(emp.getFullName());
        dto.setEmail(emp.getEmail());
        dto.setRole(emp.getRole());
        dto.setDepartment(emp.getDepartment());
        dto.setDesignation(emp.getDesignation());
        dto.setPhoneNumber(emp.getPhoneNumber());
        dto.setEnabled(emp.isEnabled());
        dto.setCreatedAt(emp.getCreatedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

