package com.vineeth.ems.controllers.validators;

import com.vineeth.ems.constants.ErrorMessages;
import com.vineeth.ems.dto.CreateEmployeeRequest;
import com.vineeth.ems.dto.SearchEmployeeRequest;
import com.vineeth.ems.exceptions.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class EmployeeRequestValidator {
    public void validateCreateEmployeeRequest(CreateEmployeeRequest createEmployeeRequest)
            throws ValidationException {
        if(createEmployeeRequest.getAge() < 18 || createEmployeeRequest.getAge() > 60) {
            throw new ValidationException(ErrorMessages.EMPLOYEE_AGE_VALIDATION_ERROR);
        }
        if(createEmployeeRequest.getSalary() <= 0) {
            throw new ValidationException(ErrorMessages.EMPLOYEE_SALARY_VALIDATION_ERROR);
        }
        if(createEmployeeRequest.getFirstName().matches(".*\\d.*") ||
                createEmployeeRequest.getLastName().matches(".*\\d.*")) {
            throw new ValidationException(ErrorMessages.EMPLOYEE_NAME_VALIDATION_ERROR);
        }
    }

    public void validateSearchEmployeeRequest(SearchEmployeeRequest searchEmployeeRequest)
            throws ValidationException {
        if(searchEmployeeRequest.getUsername() == null && searchEmployeeRequest.getAge() == null) {
            throw new ValidationException(ErrorMessages.SEARCH_REQUEST_NO_FIELD_FOUND_ERROR);
        }
        if(searchEmployeeRequest.getAge() != null &&
                (searchEmployeeRequest.getAge() < 18 || searchEmployeeRequest.getAge() > 60)) {
            throw new ValidationException(ErrorMessages.EMPLOYEE_AGE_VALIDATION_ERROR);
        }
    }
}
