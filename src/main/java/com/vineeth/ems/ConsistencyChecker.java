package com.vineeth.ems;

import com.vineeth.ems.api.PayrollEmployee;
import com.vineeth.ems.entities.Employee;
import com.vineeth.ems.entities.EmployeeStatus;
import com.vineeth.ems.entities.repository.EmployeeRepository;
import com.vineeth.ems.exceptions.PayrollServiceException;
import com.vineeth.ems.service.PayrollService;
import com.vineeth.ems.util.MDCUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ConsistencyChecker {
    private static final Logger logger = LoggerFactory.getLogger(ConsistencyChecker.class);
    private static final List<EmployeeStatus> employeeStatusToSearch = Arrays.asList(EmployeeStatus.CREATING,
            EmployeeStatus.PAYROLL_PENDING);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PayrollService payrollService;

    @Scheduled(fixedDelayString = "${consistency.fixed.delay.milliseconds}",
            initialDelayString = "${consistency.initial.delay.milliseconds}")
    public void scheduleConsistencyCheck() {
        MDCUtil.setupMDCContext();
        logger.info("Started checking for consistency");
        List<Employee> employees = filterEmployees(employeeRepository.findByEmployeeStatusIn(employeeStatusToSearch));
        if(employees.size() > 0) {
            Map<String, Integer> userNameToPayrollInfo = getUsernameToPayRollIdInPayrollService();
            deleteEmployees(employees, userNameToPayrollInfo);
        }
        logger.info("Completed consistency check");
        MDCUtil.clearMDCContext();
    }

    private List<Employee> filterEmployees(List<Employee> employees) {
        long currentEpochTime = System.currentTimeMillis();
        return employees.stream()
                .filter(employee -> ((currentEpochTime - employee.getLastUpdatedEpochTime()) / 1000) >= 300)
                .collect(Collectors.toList());
    }

    private Map<String, Integer> getUsernameToPayRollIdInPayrollService() {
        Map<String, Integer> userNameToPayrollId = new HashMap<>();
        try {
            List<PayrollEmployee> payrollEmployeeInformation = payrollService.getAllPayrolls();
            for(PayrollEmployee payrollEmployee: payrollEmployeeInformation) {
                userNameToPayrollId.put(payrollEmployee.getEmployeeName(), Integer.parseInt(payrollEmployee.getId()));
            }
        } catch (PayrollServiceException e) {
            logger.error(e.getMessage(), e);
        }
        return userNameToPayrollId;
    }

    private void deleteEmployees(List<Employee> employees, Map<String, Integer> userNameToPayrollInfo) {
        for(Employee employee: employees) {
            if(userNameToPayrollInfo.containsKey(employee.getUsername())) {
                try {
                    payrollService.deletePayrollEntry(userNameToPayrollInfo.get(employee.getUsername()));
                    employeeRepository.deleteById(employee.getId());
                } catch (PayrollServiceException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }
}
