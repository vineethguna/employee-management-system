package com.vineeth.ems;

import com.vineeth.ems.dto.CreateEmployeeRequest;
import com.vineeth.ems.dto.SearchEmployeeRequest;
import com.vineeth.ems.entities.Counter;
import com.vineeth.ems.entities.Employee;
import com.vineeth.ems.entities.EmployeeStatus;
import com.vineeth.ems.entities.repository.CounterRepository;
import com.vineeth.ems.entities.repository.EmployeeRepository;
import com.vineeth.ems.exceptions.EmployeeServiceException;
import com.vineeth.ems.exceptions.PayrollServiceException;
import com.vineeth.ems.service.EmployeeService;
import com.vineeth.ems.service.PayrollService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

import java.util.ArrayList;
import java.util.List;

public class EmployeeServiceTest {
    @InjectMocks
    private EmployeeService employeeService;

    @Mock
    private PayrollService payrollService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private CounterRepository counterRepository;

    @Mock
    private PlatformTransactionManager platformTransactionManager;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetEmployeeWithSearchRequestWithAge() {
        List<Employee> employees = new ArrayList<>();
        Employee employee = new Employee();
        employee.setAge(23);
        employees.add(employee);

        Mockito.when(employeeRepository.findByAgeAndEmployeeStatus(22, EmployeeStatus.CREATED)).thenReturn(employees);

        SearchEmployeeRequest searchEmployeeRequest = new SearchEmployeeRequest(null, 22);

        Assertions.assertEquals(employees, employeeService.getEmployeesBySearchRequest(searchEmployeeRequest));
    }

    @Test
    public void testGetEmployeeWithSearchRequestWithName() {
        List<Employee> employees = new ArrayList<>();
        Employee employee = new Employee();
        employee.setUsername("abc");
        employees.add(employee);

        Mockito.when(employeeRepository.findByUsernameContainingAndEmployeeStatus("abc", EmployeeStatus.CREATED))
                .thenReturn(employees);

        SearchEmployeeRequest searchEmployeeRequest = new SearchEmployeeRequest("abc", null);

        Assertions.assertEquals(employees, employeeService.getEmployeesBySearchRequest(searchEmployeeRequest));
    }

    @Test
    public void testGetEmployeeWithSearchRequestWithNameAndAge() {
        List<Employee> employees = new ArrayList<>();
        Employee employee = new Employee();
        employee.setUsername("abc");
        employee.setAge(22);
        employees.add(employee);

        Mockito.when(employeeRepository.findByAgeAndUsernameContainingAndEmployeeStatus(22,"abc", EmployeeStatus.CREATED))
                .thenReturn(employees);

        SearchEmployeeRequest searchEmployeeRequest = new SearchEmployeeRequest("abc", 22);

        Assertions.assertEquals(employees, employeeService.getEmployeesBySearchRequest(searchEmployeeRequest));
    }

    @Test
    public void testCreateEmployeeIfPayrollThrowsException() throws Exception {
        Mockito.when(platformTransactionManager.getTransaction(Mockito.any())).
                thenReturn(Mockito.mock(TransactionStatus.class));

        Counter counter = new Counter();
        counter.setCounter(1);
        counter.setUsername("abc.def");

        Employee employee = new Employee();

        Mockito.when(counterRepository.findByUsername("abc.def")).thenReturn(counter);
        Mockito.when(employeeRepository.save(Mockito.any())).thenReturn(employee);

        Mockito.when(payrollService.createPayrollEntryForNewEmployee(Mockito.any(), Mockito.anyInt()))
                .thenThrow(new PayrollServiceException("Payroll exception"));

        CreateEmployeeRequest createEmployeeRequest = new CreateEmployeeRequest();
        createEmployeeRequest.setSalary(120000);
        createEmployeeRequest.setAge(22);
        createEmployeeRequest.setLastName("def");
        createEmployeeRequest.setFirstName("abc");

        boolean isExceptionThrown = false;

        try {
            employeeService.createEmployee(createEmployeeRequest);
        } catch (EmployeeServiceException e) {
            isExceptionThrown = true;
        }

        Assertions.assertTrue(isExceptionThrown);
    }

    @Test
    public void testCreateEmployee() throws Exception {
        Mockito.when(platformTransactionManager.getTransaction(Mockito.any())).
                thenReturn(Mockito.mock(TransactionStatus.class));

        Counter counter = new Counter();
        counter.setCounter(1);
        counter.setUsername("abc.def");

        Employee employee = new Employee();
        employee.setAge(22);
        employee.setUsername("abc.def2");
        employee.setLastName("def");
        employee.setFirstName("abc");
        employee.setEmployeeStatus(EmployeeStatus.CREATING);

        Mockito.when(counterRepository.findByUsername("abc.def")).thenReturn(counter);
        Mockito.when(employeeRepository.save(Mockito.any())).thenReturn(employee);
        Mockito.when(payrollService.createPayrollEntryForNewEmployee(Mockito.any(), Mockito.anyInt())).thenReturn(123);

        CreateEmployeeRequest createEmployeeRequest = new CreateEmployeeRequest();
        createEmployeeRequest.setSalary(120000);
        createEmployeeRequest.setAge(22);
        createEmployeeRequest.setLastName("def");
        createEmployeeRequest.setFirstName("abc");

        Employee createdEmployee = employeeService.createEmployee(createEmployeeRequest);

        ArgumentCaptor<Employee> employeeArgumentCaptor = ArgumentCaptor.forClass(Employee.class);
        Mockito.verify(employeeRepository, Mockito.times(3)).save(employeeArgumentCaptor.capture());
        List<Employee> capturedEmployees = employeeArgumentCaptor.getAllValues();

        ArgumentCaptor<Integer> integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Employee> payrollEmployeeCaptor = ArgumentCaptor.forClass(Employee.class);
        Mockito.verify(payrollService).createPayrollEntryForNewEmployee(payrollEmployeeCaptor.capture(),
                integerArgumentCaptor.capture());

        Assertions.assertEquals("abc.def2", createdEmployee.getUsername());
        Assertions.assertEquals(EmployeeStatus.CREATED, createdEmployee.getEmployeeStatus());
        Assertions.assertEquals(123, createdEmployee.getPayrollId());

        Assertions.assertEquals(3, capturedEmployees.size());
        Assertions.assertEquals(EmployeeStatus.CREATING, capturedEmployees.get(0).getEmployeeStatus());
        //Assertions.assertEquals(EmployeeStatus.PAYROLL_PENDING, capturedEmployees.get(1).getEmployeeStatus());
        Assertions.assertEquals(EmployeeStatus.CREATED, capturedEmployees.get(2).getEmployeeStatus());

        Assertions.assertEquals(120000, integerArgumentCaptor.getValue());
        //Assertions.assertEquals(EmployeeStatus.PAYROLL_PENDING, payrollEmployeeCaptor.getValue().getEmployeeStatus());
    }
}
