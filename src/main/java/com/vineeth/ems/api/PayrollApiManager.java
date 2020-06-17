package com.vineeth.ems.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;


@Component
public class PayrollApiManager {
    private static final String CREATE_PAYROLL_API = "/api/v1/create";
    private static final String DELETE_PAYROLL_API = "/api/v1/delete/%d";
    private static final String GET_ALL_PAYROLL_API = "/api/v1/employees";
    private static final String GET_PAYROLL_FOR_EMPLOYEE_API = "/api/v1/employee/%d";

    @Autowired
    private RestTemplate restTemplate;

    @Value("${payroll.host}")
    private String payrollHost;


    public PayrollCreateResponse createPayrollForEmployee(String username, int salary, int age) {
        PayrollCreateRequest payrollCreateRequest = new PayrollCreateRequest();
        payrollCreateRequest.setName(username);
        payrollCreateRequest.setAge(Integer.toString(age));
        payrollCreateRequest.setSalary(Integer.toString(salary));

        String url = String.format("%s%s", payrollHost, CREATE_PAYROLL_API);
        HttpEntity<PayrollCreateRequest> httpRequestEntity = new HttpEntity<>(payrollCreateRequest);


        ResponseEntity<PayrollCreateResponse> response =  restTemplate.exchange(url, HttpMethod.POST,
                httpRequestEntity, PayrollCreateResponse.class);
        if(response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }
        throw new HttpServerErrorException(response.getStatusCode());
    }

    public PayrollDeleteResponse deletePayrollForEmployee(int payrollId) {
        String url = String.format("%s%s", payrollHost, String.format(DELETE_PAYROLL_API, payrollId));

        ResponseEntity<PayrollDeleteResponse> response =  restTemplate.postForEntity(url, HttpMethod.DELETE,
                PayrollDeleteResponse.class);
        if(response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }
        throw new HttpServerErrorException(response.getStatusCode());
    }

    public PayrollEmployeeResponse getEmployeePayroll(int payrollId) {
        String url = String.format("%s%s", payrollHost, String.format(GET_PAYROLL_FOR_EMPLOYEE_API, payrollId));

        ResponseEntity<PayrollEmployeeResponse> response =  restTemplate.postForEntity(url, HttpMethod.GET,
                PayrollEmployeeResponse.class);
        if(response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }
        throw new HttpServerErrorException(response.getStatusCode());
    }

    public PayrollMultiEmployeeResponse getAllEmployeesPayroll() {
        String url = String.format("%s%s", payrollHost, GET_ALL_PAYROLL_API);

        ResponseEntity<PayrollMultiEmployeeResponse> response =  restTemplate.postForEntity(url, HttpMethod.GET,
                PayrollMultiEmployeeResponse.class);

        if(response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }
        throw new HttpServerErrorException(response.getStatusCode());
    }

}
