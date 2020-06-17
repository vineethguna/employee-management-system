package com.vineeth.ems.dto;

import com.vineeth.ems.entities.Employee;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class EmployeeResponse {
    private Status status;
    private List<Employee> data;
    private String errorMessage;
}
