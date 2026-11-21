package org.evabirds.evabirds.service;

import jakarta.annotation.PostConstruct;
import org.evabirds.evabirds.dto.CreateEmployeeRequest;
import org.evabirds.evabirds.dto.EmployeeAuthResponse;
import org.evabirds.evabirds.dto.EmployeeLoginRequest;
import org.evabirds.evabirds.dto.EmployeeResponse;
import org.evabirds.evabirds.entity.Employee;
import org.evabirds.evabirds.exception.ResourceNotFoundException;
import org.evabirds.evabirds.exception.UserAlreadyExistsException;
import org.evabirds.evabirds.repository.EmployeeRepository;
import org.evabirds.evabirds.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public EmployeeService(
            EmployeeRepository employeeRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostConstruct
    public void initDefaultEmployees() {
        if (employeeRepository.count() == 0) {
            Employee superAdmin = new Employee(
                    "EVA-ADMIN-001",
                    "Sreekanth (Platform SuperAdmin)",
                    "admin@evabirds.com",
                    passwordEncoder.encode("admin123"),
                    "ROLE_ADMIN",
                    "Platform Architecture & Operations",
                    "Platform SuperAdmin"
            );
            superAdmin.setPhoneNumber("+91 81050 13318");
            employeeRepository.save(superAdmin);

            Employee auditor = new Employee(
                    "EVA-AUDIT-002",
                    "Dr. Saravanan (Lead Field Auditor)",
                    "saravanan.vet@gmail.com",
                    passwordEncoder.encode("admin123"),
                    "ROLE_ADMIN",
                    "Veterinary & Farm Audit Compliance",
                    "Lead Field Auditor"
            );
            auditor.setPhoneNumber("+91 98421 88400");
            employeeRepository.save(auditor);

            Employee ops = new Employee(
                    "EVA-OPS-003",
                    "Marcus Chen (Senior Operations Manager)",
                    "marcus.ops@evabirds.com",
                    passwordEncoder.encode("admin123"),
                    "ROLE_ADMIN",
                    "Infrastructure & Site Reliability",
                    "Senior Operations Manager"
            );
            ops.setPhoneNumber("+91 98421 77200");
            employeeRepository.save(ops);

            System.out.println("Default platform employees initialized successfully in 'employees' table.");
        }
    }

    @Transactional(readOnly = true)
    public EmployeeAuthResponse login(EmployeeLoginRequest request) {
        String idTrimmed = request.getIdentifier().trim();

        Employee employee = employeeRepository.findByEmployeeIdIgnoreCaseOrEmailIgnoreCase(idTrimmed, idTrimmed)
                .orElseThrow(() -> new BadCredentialsException("Invalid Employee ID/Email or password"));

        if (!passwordEncoder.matches(request.getPassword(), employee.getPassword())) {
            throw new BadCredentialsException("Invalid Employee ID/Email or password");
        }

        if (!employee.isEnabled()) {
            throw new BadCredentialsException("Employee account is disabled. Contact your supervisor.");
        }

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", employee.getRole());
        extraClaims.put("employeeId", employee.getEmployeeId());
        extraClaims.put("department", employee.getDepartment());
        extraClaims.put("fullName", employee.getFullName());
        extraClaims.put("isEmployee", true);

        String token = jwtService.generateToken(extraClaims, employee);
        return new EmployeeAuthResponse(token, EmployeeResponse.fromEntity(employee));
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(EmployeeResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        return EmployeeResponse.fromEntity(employee);
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeByEmail(String email) {
        Employee employee = employeeRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with email: " + email));
        return EmployeeResponse.fromEntity(employee);
    }

    @Transactional
    public EmployeeResponse createEmployee(CreateEmployeeRequest request) {
        if (employeeRepository.existsByEmployeeIdIgnoreCase(request.getEmployeeId())) {
            throw new UserAlreadyExistsException("Employee with ID '" + request.getEmployeeId() + "' already exists");
        }

        if (employeeRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new UserAlreadyExistsException("Employee with email '" + request.getEmail() + "' already exists");
        }

        Employee employee = new Employee(
                request.getEmployeeId().trim().toUpperCase(),
                request.getFullName().trim(),
                request.getEmail().trim().toLowerCase(),
                passwordEncoder.encode(request.getPassword()),
                request.getRole() != null ? request.getRole() : "ROLE_ADMIN",
                request.getDepartment() != null ? request.getDepartment() : "Platform Operations",
                request.getDesignation() != null ? request.getDesignation() : "Staff Member"
        );
        employee.setPhoneNumber(request.getPhoneNumber());

        Employee saved = employeeRepository.save(employee);
        return EmployeeResponse.fromEntity(saved);
    }

    @Transactional
    public EmployeeResponse toggleEmployeeStatus(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        employee.setEnabled(!employee.isEnabled());
        Employee updated = employeeRepository.save(employee);
        return EmployeeResponse.fromEntity(updated);
    }

    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        employeeRepository.delete(employee);
    }
}

