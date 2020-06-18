package com.vineeth.ems;

import com.vineeth.ems.api.*;
import com.vineeth.ems.entities.Employee;
import com.vineeth.ems.exceptions.PayrollServiceException;
import com.vineeth.ems.service.PayrollService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;
import sun.jvm.hotspot.utilities.Assert;

import java.util.ArrayList;
import java.util.List;

public class PayrollServiceTest {
    @InjectMocks
    private PayrollService payrollService;

    @Mock
    private PayrollApiManager payrollApiManager;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreatePayrollEntryIfAPIThrowsException() {
        String username = "abc";
        int salary = 120000;
        int age = 22;

        Mockito.when(payrollApiManager.createPayrollForEmployee(username, salary, age))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        Employee employee = new Employee();
        employee.setUsername("abc");
        employee.setAge(22);

        boolean isExceptionThrown = false;
        try {
            payrollService.createPayrollEntryForNewEmployee(employee, salary);
        } catch (PayrollServiceException p) {
            Assertions.assertEquals("Error creating payroll. Got http error status code 500", p.getMessage());
            isExceptionThrown = true;
        }
        Assertions.assertTrue(isExceptionThrown);
    }

    @Test
    public void testCreatePayrollEntry() throws Exception {
        PayrollCreateResponse.EmployeeData employeeData = new PayrollCreateResponse().new EmployeeData();
        employeeData.setId("123");

        PayrollCreateResponse payrollCreateResponse = new PayrollCreateResponse();
        payrollCreateResponse.setStatus("success");
        payrollCreateResponse.setData(employeeData);

        Employee employee = new Employee();
        employee.setUsername("abc");
        employee.setAge(22);
        Mockito.when(payrollApiManager.createPayrollForEmployee("abc", 120000, 22))
                .thenReturn(payrollCreateResponse);

        int payrollId = payrollService.createPayrollEntryForNewEmployee(employee, 120000);

        Assertions.assertEquals(123, payrollId);
    }

    @Test
    public void testDeletePayrollEntryIfAPIThrowsException() {
        int payrollId = 123;

        Mockito.when(payrollApiManager.deletePayrollForEmployee(123))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        boolean isExceptionThrown = false;
        try {
            payrollService.deletePayrollEntry(payrollId);
        } catch (PayrollServiceException p) {
            Assertions.assertEquals("Error deleting payroll entry. Got http error status code 500",
                    p.getMessage());
            isExceptionThrown = true;
        }
        Assertions.assertTrue(isExceptionThrown);
    }

    @Test
    public void testDeletePayrollEntryIfDeleteSucceeds() throws Exception {
        int payrollId = 123;

        PayrollDeleteResponse payrollDeleteResponse = new PayrollDeleteResponse();
        payrollDeleteResponse.setStatus("success");
        payrollDeleteResponse.setMessage("Successfully deleted");

        Mockito.when(payrollApiManager.deletePayrollForEmployee(payrollId)).thenReturn(payrollDeleteResponse);

        Assertions.assertTrue(payrollService.deletePayrollEntry(payrollId));
    }

    @Test
    public void testDeletePayrollEntryIfDeleteFails() throws Exception {
        int payrollId = 123;

        PayrollDeleteResponse payrollDeleteResponse = new PayrollDeleteResponse();
        payrollDeleteResponse.setStatus("failed");
        payrollDeleteResponse.setMessage("Failed to delete");

        Mockito.when(payrollApiManager.deletePayrollForEmployee(payrollId)).thenReturn(payrollDeleteResponse);

        Assertions.assertFalse(payrollService.deletePayrollEntry(payrollId));
    }

    @Test
    public void testGetAllPayrollsIfAPIThrowsException() {
        Mockito.when(payrollApiManager.getAllEmployeesPayroll())
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        boolean isExceptionThrown = false;
        try {
            payrollService.getAllPayrolls();
        } catch (PayrollServiceException p) {
            Assertions.assertEquals("Error fetching all payroll entries. Got http error status code 500",
                    p.getMessage());
            isExceptionThrown = true;
        }
        Assertions.assertTrue(isExceptionThrown);
    }

    @Test
    public void testGetAllPayrolls() throws Exception {
        List<PayrollEmployee> payrollEmployees = new ArrayList<>();
        PayrollEmployee payrollEmployee = new PayrollEmployee();
        payrollEmployee.setId("345");

        PayrollMultiEmployeeResponse payrollMultiEmployeeResponse = new PayrollMultiEmployeeResponse();
        payrollMultiEmployeeResponse.setStatus("success");
        payrollMultiEmployeeResponse.setData(payrollEmployees);

        Mockito.when(payrollApiManager.getAllEmployeesPayroll()).thenReturn(payrollMultiEmployeeResponse);

        Assertions.assertEquals(payrollEmployees, payrollService.getAllPayrolls());
    }

    @Test
    public void testGetPayrollByIDIfAPIThrowsException() {
        int payrollId = 123;

        Mockito.when(payrollApiManager.getEmployeePayroll(payrollId))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        boolean isExceptionThrown = false;
        try {
            payrollService.getPayrollById(payrollId);
        } catch (PayrollServiceException p) {
            Assertions.assertEquals("Error fetching all payroll entry for id 123. Got http error status code 500",
                    p.getMessage());
            isExceptionThrown = true;
        }
        Assertions.assertTrue(isExceptionThrown);
    }

    @Test
    public void testGetPayrollByID() throws Exception {
        int payrollId = 123;
        PayrollEmployee payrollEmployee = new PayrollEmployee();
        payrollEmployee.setId("345");

        PayrollEmployeeResponse payrollEmployeeResponse = new PayrollEmployeeResponse();
        payrollEmployeeResponse.setStatus("success");
        payrollEmployeeResponse.setData(payrollEmployee);

        Mockito.when(payrollApiManager.getEmployeePayroll(payrollId)).thenReturn(payrollEmployeeResponse);

        Assertions.assertEquals(payrollEmployee, payrollService.getPayrollById(payrollId));
    }
}
