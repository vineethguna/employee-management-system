package com.vineeth.ems.api;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayrollCreateRequest {
    private String name;
    private String salary;
    private String age;
}
