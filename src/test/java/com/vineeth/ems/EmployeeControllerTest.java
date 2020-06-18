package com.vineeth.ems;

import com.vineeth.ems.controllers.EmployeeController;
import com.vineeth.ems.controllers.validators.EmployeeRequestValidator;
import com.vineeth.ems.dto.CreateEmployeeRequest;
import com.vineeth.ems.dto.EmployeeResponse;
import com.vineeth.ems.dto.SearchEmployeeRequest;
import com.vineeth.ems.entities.Employee;
import com.vineeth.ems.exceptions.EmployeeServiceException;
import com.vineeth.ems.service.EmployeeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public class EmployeeControllerTest {
    @InjectMocks
    private EmployeeController employeeController;

    @Mock
    private EmployeeService employeeService;

    @Spy
    private EmployeeRequestValidator employeeRequestValidator = new EmployeeRequestValidator();

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateEmployeeWithNegativeAge() throws Exception {
        CreateEmployeeRequest c = new CreateEmployeeRequest();
        c.setFirstName("abc");
        c.setLastName("def");
        c.setAge(-18);
        c.setSalary(120000);

        ResponseEntity<EmployeeResponse> employeeResponse = employeeController.createEmployee(c);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, employeeResponse.getStatusCode());
    }

    @Test
    public void testCreateEmployeeWithNegativeSalary() {
        CreateEmployeeRequest c = new CreateEmployeeRequest();
        c.setFirstName("abc");
        c.setLastName("def");
        c.setAge(18);
        c.setSalary(-120000);

        ResponseEntity<EmployeeResponse> employeeResponse = employeeController.createEmployee(c);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, employeeResponse.getStatusCode());
    }

    @Test
    public void testCreateEmployeeWithNegativeSalaryAndNegativeAge() {
        CreateEmployeeRequest c = new CreateEmployeeRequest();
        c.setFirstName("abc");
        c.setLastName("def");
        c.setAge(-18);
        c.setSalary(-120000);

        ResponseEntity<EmployeeResponse> employeeResponse = employeeController.createEmployee(c);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, employeeResponse.getStatusCode());
    }

    @Test
    public void testCreateEmployeeWithNumbersInFirstNameAndLastName() {
        CreateEmployeeRequest c = new CreateEmployeeRequest();
        c.setFirstName("abc1");
        c.setLastName("def2");
        c.setAge(18);
        c.setSalary(120000);

        ResponseEntity<EmployeeResponse> employeeResponse = employeeController.createEmployee(c);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, employeeResponse.getStatusCode());
    }

    @Test
    public void testCreateEmployeeIfEmployeeServiceThrowsException() throws Exception {
        Mockito.when(employeeService.createEmployee(Mockito.any()))
                .thenThrow(new EmployeeServiceException("Sample exception"));

        CreateEmployeeRequest c = new CreateEmployeeRequest();
        c.setFirstName("abc");
        c.setLastName("def");
        c.setAge(18);
        c.setSalary(120000);

        ResponseEntity<EmployeeResponse> employeeResponse = employeeController.createEmployee(c);

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, employeeResponse.getStatusCode());
        Assertions.assertEquals("Sample exception", employeeResponse.getBody().getErrorMessage());
    }

    @Test
    public void testCreateEmployeeWithoutErrors() throws Exception {
        CreateEmployeeRequest c = new CreateEmployeeRequest();
        c.setFirstName("abc");
        c.setLastName("def");
        c.setAge(18);
        c.setSalary(120000);

        Employee newEmployee = new Employee();
        newEmployee.setFirstName("abc");
        newEmployee.setLastName("def");

        Mockito.when(employeeService.createEmployee(c)).thenReturn(newEmployee);

        ResponseEntity<EmployeeResponse> employeeResponse = employeeController.createEmployee(c);
        Assertions.assertEquals(HttpStatus.OK, employeeResponse.getStatusCode());
        Assertions.assertEquals(newEmployee, employeeResponse.getBody().getData().get(0));
    }

    @Test
    public void testSearchEmployeeWithNameAndAgeAsNull() {
        ResponseEntity<EmployeeResponse> employeeResponse = employeeController.searchEmployee(null, null);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, employeeResponse.getStatusCode());
    }

    @Test
    public void testSearchEmployeeWithNegativeAge() {
        ResponseEntity<EmployeeResponse> employeeResponse = employeeController.searchEmployee(null, -123);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, employeeResponse.getStatusCode());
    }

    @Test
    public void testSearchEmployee() {
        List<Employee> employeeList = new ArrayList<>();
        Employee newEmployee = new Employee();
        newEmployee.setFirstName("abc");
        newEmployee.setLastName("def");
        employeeList.add(newEmployee);

        Mockito.when(employeeService.getEmployeesBySearchRequest(Mockito.any(SearchEmployeeRequest.class)))
                .thenReturn(employeeList);

        ResponseEntity<EmployeeResponse> employeeResponse = employeeController.searchEmployee("abc", 23);

        ArgumentCaptor<SearchEmployeeRequest> searchEmployeeRequestArgumentCaptor =
                ArgumentCaptor.forClass(SearchEmployeeRequest.class);
        Mockito.verify(employeeService).getEmployeesBySearchRequest(searchEmployeeRequestArgumentCaptor.capture());

        Assertions.assertEquals(HttpStatus.OK, employeeResponse.getStatusCode());
        Assertions.assertEquals(employeeList, employeeResponse.getBody().getData());
        Assertions.assertEquals(23, searchEmployeeRequestArgumentCaptor.getValue().getAge());
        Assertions.assertEquals("abc", searchEmployeeRequestArgumentCaptor.getValue().getUsername());
    }
}
