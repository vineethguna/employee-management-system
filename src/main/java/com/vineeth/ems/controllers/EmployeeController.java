package com.vineeth.ems.controllers;

import com.vineeth.ems.controllers.validators.EmployeeRequestValidator;
import com.vineeth.ems.dto.*;
import com.vineeth.ems.entities.Employee;
import com.vineeth.ems.exceptions.EmployeeServiceException;
import com.vineeth.ems.exceptions.ValidationException;
import com.vineeth.ems.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/api/v1/employee")
public class EmployeeController {

    @Autowired
    private EmployeeRequestValidator employeeRequestValidator;

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/create")
    public ResponseEntity<EmployeeResponse> createEmployee(@RequestBody CreateEmployeeRequest createEmployeeRequest) {
        try {
            employeeRequestValidator.validateCreateEmployeeRequest(createEmployeeRequest);
            Employee employee = employeeService.createEmployee(createEmployeeRequest);
            return new ResponseEntity<>(new EmployeeResponse(Status.SUCCESS, Collections.singletonList(employee), ""),
                    HttpStatus.OK);
        } catch (EmployeeServiceException e) {
            return new ResponseEntity<>(new EmployeeResponse(Status.FAILED, null, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ValidationException e) {
            return new ResponseEntity<>(new EmployeeResponse(Status.FAILED, null, e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<EmployeeResponse> searchEmployee(@RequestParam(value = "name", required = false) String name,
                                                           @RequestParam(value = "age", required = false) Integer age) {
        try {
            SearchEmployeeRequest searchEmployeeRequest = new SearchEmployeeRequest(name, age);
            employeeRequestValidator.validateSearchEmployeeRequest(searchEmployeeRequest);
            List<Employee> employees = employeeService.getEmployeesBySearchRequest(searchEmployeeRequest);
            return new ResponseEntity<>(new EmployeeResponse(Status.SUCCESS, employees, ""),
                    HttpStatus.OK);
        } catch (ValidationException e) {
            return new ResponseEntity<>(new EmployeeResponse(Status.FAILED, null, e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
