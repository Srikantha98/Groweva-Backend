package org.evabirds.evabirds.controller;

import org.evabirds.evabirds.dto.DashboardKPIsResponse;
import org.evabirds.evabirds.dto.FarmResponse;
import org.evabirds.evabirds.entity.FarmStatus;
import org.evabirds.evabirds.repository.EmployeeRepository;
import org.evabirds.evabirds.repository.FarmRepository;
import org.evabirds.evabirds.repository.MessageRepository;
import org.evabirds.evabirds.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final FarmRepository farmRepository;
    private final MessageRepository messageRepository;

    public AdminDashboardController(
            UserRepository userRepository,
            EmployeeRepository employeeRepository,
            FarmRepository farmRepository,
            MessageRepository messageRepository
    ) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.farmRepository = farmRepository;
        this.messageRepository = messageRepository;
    }

    @GetMapping("/dashboard/kpis")
    public ResponseEntity<DashboardKPIsResponse> getDashboardKPIs() {
        // Real customer users strictly from `users` table
        long totalUsers = userRepository.count();

        // Internal corporate staff strictly from `employees` table
        long totalEmployees = employeeRepository.count();

        long totalFarms = farmRepository.count();
        long activeFarms = farmRepository.countByStatus(FarmStatus.ACTIVE);
        long pendingReviewFarms = farmRepository.countByStatus(FarmStatus.DRAFT);
        long rentedLeasedFarms = farmRepository.countByStatus(FarmStatus.RENTED) + farmRepository.countByStatus(FarmStatus.LEASED);
        long totalBirdCapacity = farmRepository.sumActiveBirdCapacity();
        long totalVisits = farmRepository.sumTotalViews();
        long monthlyInquiries = messageRepository.count();

        DashboardKPIsResponse response = new DashboardKPIsResponse(
                totalUsers,
                totalEmployees,
                totalFarms,
                activeFarms,
                pendingReviewFarms,
                rentedLeasedFarms,
                totalBirdCapacity,
                totalVisits,
                monthlyInquiries,
                "OPERATIONAL"
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/farms")
    public ResponseEntity<List<FarmResponse>> getAllFarmsForAdmin() {
        List<FarmResponse> allFarms = farmRepository.findAll()
                .stream()
                .map(FarmResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(allFarms);
    }
}

