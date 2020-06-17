package com.vineeth.ems.service;

import com.vineeth.ems.api.*;
import com.vineeth.ems.entities.Employee;
import com.vineeth.ems.exceptions.PayrollServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;

@Component
public class PayrollService {

    @Autowired
    private PayrollApiManager payrollApiManager;

    public int createPayrollEntryForNewEmployee(Employee employee, int salary) throws PayrollServiceException {
        try {
            PayrollCreateResponse response = payrollApiManager
                    .createPayrollForEmployee(employee.getUsername(), salary, employee.getAge());
            return Integer.parseInt(response.getData().getId());
        } catch (HttpServerErrorException e) {
            throw new PayrollServiceException();
        }
    }

    public boolean deletePayrollEntry(int payrollId) throws PayrollServiceException {
        try {
            PayrollDeleteResponse response = payrollApiManager.deletePayrollForEmployee(payrollId);
            return response.getStatus().equals("success");
        } catch (HttpServerErrorException e) {
            throw new PayrollServiceException();
        }
    }

    public List<PayrollEmployee> getAllPayrolls() throws PayrollServiceException {
        try {
            PayrollMultiEmployeeResponse response = payrollApiManager.getAllEmployeesPayroll();
            return response.getData();
        } catch (HttpServerErrorException e) {
            throw new PayrollServiceException();
        }
    }

    public PayrollEmployee getPayrollById(int payrollId) throws PayrollServiceException {
        try {
            PayrollEmployeeResponse response = payrollApiManager.getEmployeePayroll(payrollId);
            return response.getData();
        } catch (HttpServerErrorException e) {
            throw new PayrollServiceException();
        }
    }
}
