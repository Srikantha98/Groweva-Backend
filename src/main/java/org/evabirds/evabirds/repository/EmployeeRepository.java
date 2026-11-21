package org.evabirds.evabirds.repository;

import org.evabirds.evabirds.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmployeeIdIgnoreCase(String employeeId);

    Optional<Employee> findByEmailIgnoreCase(String email);

    Optional<Employee> findByEmployeeIdIgnoreCaseOrEmailIgnoreCase(String employeeId, String email);

    boolean existsByEmployeeIdIgnoreCase(String employeeId);

    boolean existsByEmailIgnoreCase(String email);
}

