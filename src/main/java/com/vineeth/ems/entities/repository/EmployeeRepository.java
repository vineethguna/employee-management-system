package com.vineeth.ems.entities.repository;

import com.vineeth.ems.entities.Employee;
import com.vineeth.ems.entities.EmployeeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    public List<Employee> findByAgeAndUsernameContainingAndEmployeeStatus(int age, String username,
                                                                          EmployeeStatus employeeStatus);

    public List<Employee> findByAgeAndEmployeeStatus(int age, EmployeeStatus employeeStatus);

    public List<Employee> findByUsernameContainingAndEmployeeStatus(String username, EmployeeStatus employeeStatus);

    public List<Employee> findByEmployeeStatusIn(List<EmployeeStatus> employeeStatuses);
}
