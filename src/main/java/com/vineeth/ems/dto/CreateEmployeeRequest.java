package com.vineeth.ems.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateEmployeeRequest {
    private String firstName;
    private String lastName;
    private int age;
    private int salary;
}
