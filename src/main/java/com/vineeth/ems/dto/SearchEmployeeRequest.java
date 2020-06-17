package com.vineeth.ems.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SearchEmployeeRequest {
    private String username;
    private Integer age;
}
