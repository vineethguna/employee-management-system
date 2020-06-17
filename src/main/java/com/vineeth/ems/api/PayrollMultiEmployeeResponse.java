package com.vineeth.ems.api;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PayrollMultiEmployeeResponse {
    private String status;
    private List<PayrollEmployee> data;
}
