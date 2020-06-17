package com.vineeth.ems.api;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayrollEmployeeResponse {
    private String status;
    private PayrollEmployee data;
}
