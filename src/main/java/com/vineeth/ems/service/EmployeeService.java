package com.vineeth.ems.service;

import com.vineeth.ems.dto.CreateEmployeeRequest;
import com.vineeth.ems.dto.SearchEmployeeRequest;
import com.vineeth.ems.entities.Counter;
import com.vineeth.ems.entities.Employee;
import com.vineeth.ems.entities.EmployeeStatus;
import com.vineeth.ems.entities.repository.CounterRepository;
import com.vineeth.ems.entities.repository.EmployeeRepository;
import com.vineeth.ems.exceptions.EmployeeServiceException;
import com.vineeth.ems.exceptions.PayrollServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.transaction.Transactional;
import java.util.List;

@Component
public class EmployeeService {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CounterRepository counterRepository;

    @Autowired
    private PayrollService payrollService;

    @Autowired
    private PlatformTransactionManager transactionManager;


    public List<Employee> getEmployeesBySearchRequest(SearchEmployeeRequest searchEmployeeRequest) {
        if(searchEmployeeRequest.getAge() != null && searchEmployeeRequest.getUsername() != null) {
            return employeeRepository.findByAgeAndUsernameContainingAndEmployeeStatus(searchEmployeeRequest.getAge(),
                    searchEmployeeRequest.getUsername(), EmployeeStatus.CREATED);
        } else if(searchEmployeeRequest.getAge() != null) {
            return employeeRepository.findByAgeAndEmployeeStatus(searchEmployeeRequest.getAge(),
                    EmployeeStatus.CREATED);
        } else {
            return employeeRepository.findByUsernameContainingAndEmployeeStatus(searchEmployeeRequest.getUsername(),
                    EmployeeStatus.CREATED);
        }
    }

    public Employee createEmployee(CreateEmployeeRequest employeeRequest) throws EmployeeServiceException {
        try {
            Employee newEmployee = createEmployeeEntry(employeeRequest.getFirstName().toLowerCase(),
                    employeeRequest.getLastName().toLowerCase(), employeeRequest.getAge());

            newEmployee.setEmployeeStatus(EmployeeStatus.PAYROLL_PENDING);
            newEmployee.setLastUpdatedEpochTime(System.currentTimeMillis());
            employeeRepository.save(newEmployee);

            int payrollId = payrollService.createPayrollEntryForNewEmployee(newEmployee, employeeRequest.getSalary());

            newEmployee.setEmployeeStatus(EmployeeStatus.CREATED);
            newEmployee.setPayrollId(payrollId);
            newEmployee.setLastUpdatedEpochTime(System.currentTimeMillis());
            return employeeRepository.save(newEmployee);
        } catch (PayrollServiceException p) {
            logger.error("Error creating employee", p);
            throw new EmployeeServiceException(String.format(
                    "Error creating employee due to exception in payroll service %s", p.getMessage()));
        }
    }

    public void deleteEmployeeById(long employeeId) {
        employeeRepository.deleteById(employeeId);
    }

    private Employee createEmployeeEntry(String firstName, String lastName, int age) {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        return template.execute(status -> {
            String username = String.format("%s.%s", firstName, lastName);
            int counterForUsername = updateAndGetCounterForUsername(username);
            Employee employee = new Employee();
            employee.setFirstName(firstName);
            employee.setLastName(lastName);
            employee.setAge(age);
            if(counterForUsername != 0) {
                employee.setUsername(String.format("%s%d", username, counterForUsername));
            } else {
                employee.setUsername(username);
            }
            employee.setEmployeeStatus(EmployeeStatus.CREATING);
            employee.setLastUpdatedEpochTime(System.currentTimeMillis());
            return employeeRepository.save(employee);
        });
    }

    private int updateAndGetCounterForUsername(String username) {
        Counter counter = counterRepository.findByUsername(username);
        if(counter == null) {
            counter = new Counter();
            counter.setCounter(0);
            counter.setUsername(username);
        } else {
            counter.setCounter(counter.getCounter() + 1);
        }
        counterRepository.save(counter);
        return counter.getCounter();
    }
}
