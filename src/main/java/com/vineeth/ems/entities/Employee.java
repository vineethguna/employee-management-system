package com.vineeth.ems.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Indexed;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(indexes = {@Index(columnList = "username", name = "username_index"),
        @Index(columnList = "age", name = "age_index"), @Index(columnList = "employeeStatus", name = "status_index")})
public class Employee {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String username;

    private String firstName;

    private String lastName;

    private int age;

    @Column(unique = true)
    private int payrollId;

    private EmployeeStatus employeeStatus;

    private long lastUpdatedEpochTime;
}
