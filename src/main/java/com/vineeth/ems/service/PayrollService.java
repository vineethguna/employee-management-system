package com.vineeth.ems.service;

import com.vineeth.ems.api.*;
import com.vineeth.ems.entities.Employee;
import com.vineeth.ems.exceptions.PayrollServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;

@Component
public class PayrollService {
    private static final Logger logger = LoggerFactory.getLogger(PayrollService.class);

    @Autowired
    private PayrollApiManager payrollApiManager;

    public int createPayrollEntryForNewEmployee(Employee employee, int salary) throws PayrollServiceException {
        try {
            PayrollCreateResponse response = payrollApiManager
                    .createPayrollForEmployee(employee.getUsername(), salary, employee.getAge());
            return Integer.parseInt(response.getData().getId());
        } catch (HttpServerErrorException e) {
            logger.error("Error creating payroll", e);
            throw new PayrollServiceException(String.format("Error creating payroll. Got http error status code %d",
                    e.getRawStatusCode()));
        }
    }

    public boolean deletePayrollEntry(int payrollId) throws PayrollServiceException {
        try {
            PayrollDeleteResponse response = payrollApiManager.deletePayrollForEmployee(payrollId);
            return response.getStatus().equals("success");
        } catch (HttpServerErrorException e) {
            logger.error("Error deleting payroll entry", e);
            throw new PayrollServiceException(String.format("Error deleting payroll entry. Got http error status code %d",
                    e.getRawStatusCode()));
        }
    }

    public List<PayrollEmployee> getAllPayrolls() throws PayrollServiceException {
        try {
            PayrollMultiEmployeeResponse response = payrollApiManager.getAllEmployeesPayroll();
            return response.getData();
        } catch (HttpServerErrorException e) {
            logger.error("Error fetching all payroll entries", e);
            throw new PayrollServiceException(String.format("Error fetching all payroll entries. Got http error status code %d",
                    e.getRawStatusCode()));
        }
    }

    public PayrollEmployee getPayrollById(int payrollId) throws PayrollServiceException {
        try {
            PayrollEmployeeResponse response = payrollApiManager.getEmployeePayroll(payrollId);
            return response.getData();
        } catch (HttpServerErrorException e) {
            logger.error(String.format("Error fetching payroll entry for id %d", payrollId), e);
            throw new PayrollServiceException(String.format("Error fetching all payroll entry for id %d. Got http error status code %d",
                    payrollId, e.getRawStatusCode()));
        }
    }
}
