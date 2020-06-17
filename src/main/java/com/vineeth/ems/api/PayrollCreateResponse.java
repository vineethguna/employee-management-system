package com.vineeth.ems.api;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayrollCreateResponse {
    @Getter
    @Setter
    public class EmployeeData {
        private String name;
        private String salary;
        private String age;
        private String id;
    }

    private String status;
    private EmployeeData data;
}
